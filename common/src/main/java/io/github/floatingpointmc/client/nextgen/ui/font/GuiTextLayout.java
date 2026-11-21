/*
 * FloatClient-nextgen - A free auxiliary mod for Minecraft Java Edition.
 * Copyright (C) 2026  FloatingPoint-MC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.floatingpointmc.client.nextgen.ui.font;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.awt.FontFormatException;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.text.Bidi;
import java.util.*;

/** Shaped glyph positions and advances in logical GUI pixels, independent of atlas residency. */
public final class GuiTextLayout {
    static final FontRenderContext CONTEXT = new FontRenderContext(null, true, true);
    private static final java.awt.Font FALLBACK = new java.awt.Font(java.awt.Font.DIALOG, java.awt.Font.PLAIN, 1);
    static final Identifier REGULAR = Identifier.fromNamespaceAndPath("floatclient_nextgen", "fonts/geist.ttf");
    static final Identifier BOLD = Identifier.fromNamespaceAndPath("floatclient_nextgen", "fonts/geist-bold.ttf");
    private static final Map<Identifier, java.awt.Font> FACES = bounded(32);
    private static final Map<Key, GuiTextLayout> CACHE = bounded(256);

    private record Span(String text, Style style) {}
    private record Key(List<Span> spans, Identifier face, float size) {}
    record Glyph(java.awt.Font face, int id, float x, float baseline, Integer rgb) {}
    private record Run(int start, int end, java.awt.Font face, Integer rgb) {}
    final List<Glyph> glyphs = new ArrayList<>();
    private float width, height;
    public float width() { return width; }
    public float height() { return height; }

    static <K,V> Map<K,V> bounded(int limit) {
        return new LinkedHashMap<>(16, .75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<K,V> entry) { return size() > limit; }
        };
    }

    private static java.awt.Font load(Identifier id) {
        try (var stream = Minecraft.getInstance().getResourceManager().open(id)) {
            return java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, stream);
        } catch (IOException | FontFormatException e) {
            LogUtils.getLogger().warn("Could not load GUI fonts {}", id, e);
            return FALLBACK;
        }
    }

    public static GuiTextLayout measure(String text, Identifier face, float size) {
        if (!Float.isFinite(size) || size <= 0) throw new IllegalArgumentException("Expected a positive fonts size");
        var spans = new ArrayList<Span>();
        spans.add(new Span(text, Style.EMPTY));
        var key = new Key(List.copyOf(spans), face, size);
        return CACHE.computeIfAbsent(key, GuiTextLayout::new);
    }

    private GuiTextLayout(Key key) {
        var line = new StringBuilder();
        var runs = new ArrayList<Run>();
        java.awt.Font defaultFont = FACES.computeIfAbsent(key.face == null ? REGULAR : key.face, GuiTextLayout::load).deriveFont(key.size);
        for (var span : key.spans) {
            Style style = span.style;
            Identifier file = key.face != null ? key.face : style.isBold() ? BOLD : REGULAR;
            int flags = (style.isBold() ? java.awt.Font.BOLD : 0) | (style.isItalic() ? java.awt.Font.ITALIC : 0);
            java.awt.Font face = FACES.computeIfAbsent(file, GuiTextLayout::load).deriveFont(flags, key.size)
                    .deriveFont(Map.of(TextAttribute.KERNING, TextAttribute.KERNING_ON));
            java.awt.Font fallback = FALLBACK.deriveFont(flags, key.size);
            Integer rgb = style.getColor() == null ? null : style.getColor().getValue();
            for (int i = 0; i < span.text.length();) {
                int cp = span.text.codePointAt(i);
                i += Character.charCount(cp);
                if (cp == '\n') {
                    shapeLine(line, runs, defaultFont);
                    runs.clear();
                    line.setLength(0);
                    continue;
                }
                java.awt.Font chosen = face.canDisplay(cp) ? face : fallback;
                int start = line.length();
                line.appendCodePoint(cp);
                if (!runs.isEmpty()) {
                    Run last = runs.getLast();
                    if (last.face.equals(chosen) && Objects.equals(last.rgb, rgb)) {
                        runs.set(runs.size()-1, new Run(last.start, line.length(), chosen, rgb));
                        continue;
                    }
                }
                runs.add(new Run(start, line.length(), chosen, rgb));
            }
        }
        shapeLine(line, runs, defaultFont);
    }

    private void shapeLine(StringBuilder line, List<Run> runs, java.awt.Font defaultFont) {
        var metrics = defaultFont.getLineMetrics("Ag", CONTEXT);
        float ascent = metrics.getAscent(), below = metrics.getDescent() + metrics.getLeading();
        for (Run run : runs) {
            var m = run.face.getLineMetrics("Ag", CONTEXT);
            ascent = Math.max(ascent, m.getAscent());
            below = Math.max(below, m.getDescent() + m.getLeading());
        }
        float baseline = height + ascent;
        height += ascent + below;
        if (line.isEmpty()) return;
        char[] chars = line.toString().toCharArray();
        Bidi bidi = new Bidi(chars, 0, null, 0, chars.length, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
        int count = bidi.getRunCount();
        byte[] levels = new byte[count];
        Integer[] order = new Integer[count];
        for (int i = 0; i < count; i++) {
            levels[i] = (byte)bidi.getRunLevel(i);
            order[i] = i;
        }
        Bidi.reorderVisually(levels, 0, order, 0, count);
        float pen = 0;
        for (int index : order) {
            boolean rtl = (levels[index] & 1) != 0;
            for (Run run : rtl ? runs.reversed() : runs) {
                int start = Math.max(run.start, bidi.getRunStart(index));
                int end = Math.min(run.end, bidi.getRunLimit(index));
                if (end <= start) continue;
                var vector = run.face.layoutGlyphVector(CONTEXT, chars, start, end,
                        rtl ? java.awt.Font.LAYOUT_RIGHT_TO_LEFT : java.awt.Font.LAYOUT_LEFT_TO_RIGHT);
                for (int g = 0; g < vector.getNumGlyphs(); g++) {
                    var p = vector.getGlyphPosition(g);
                    glyphs.add(new Glyph(run.face, vector.getGlyphCode(g), pen + (float)p.getX(),
                            baseline + (float)p.getY(), run.rgb));
                }
                pen += (float)vector.getGlyphPosition(vector.getNumGlyphs()).getX();
            }
        }
        width = Math.max(width, pen);
    }

    static void clear() { CACHE.clear(); FACES.clear(); }
}
