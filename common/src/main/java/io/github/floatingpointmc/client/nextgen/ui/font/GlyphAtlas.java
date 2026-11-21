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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

/** Render-thread owned. Pages referenced by queued GUI elements survive until submission completes. */
public final class GlyphAtlas {
    static final int SIDE = 512;
    private static final int RETAINED_PAGES = 8;
    private static final ArrayList<Page> pages = new ArrayList<>();
    private static final Map<Key, Tile> tiles = new HashMap<>();
    private static long frame;
    private record Key(java.awt.Font font, int id, float density) {}
    record Tile(Page page, int u, int v, int w, int h, float left, float top, float scale) {}

    static final class Page {
        final DynamicTexture texture = new DynamicTexture("FloatClient glyph coverage", SIDE, SIDE, true);
        final TextureSetup setup = TextureSetup.singleTexture(texture.getTextureView(),
                RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
        int x, y, rowHeight;
        long lastUsed;
        boolean dirty;

        boolean fit(int width, int height) {
            if (x + width > SIDE) {
                if (y + rowHeight + height > SIDE) return false;
                x = 0;
                y += rowHeight;
                rowHeight = 0;
            }
            return y + height <= SIDE;
        }
    }

    static Tile glyph(java.awt.Font font, int id, float density) {
        // Bound the bitmap work for extreme zoom while preserving logical size.
        density = Math.min(density, 192f / font.getSize2D());
        var key = new Key(font, id, density);
        var cached = tiles.get(key);
        if (cached != null) {
            cached.page.lastUsed = frame;
            return cached;
        }
        java.awt.Font raster = font.deriveFont(font.getSize2D() * density * 2);
        var vector = raster.createGlyphVector(GuiTextLayout.CONTEXT, new int[]{id});
        var ink = vector.getPixelBounds(GuiTextLayout.CONTEXT, 0, 0);
        if (ink.isEmpty()) return null;
        int left = Math.floorDiv(ink.x, 2), top = Math.floorDiv(ink.y, 2);
        int w = Math.ceilDiv(ink.x + ink.width, 2) - left;
        int h = Math.ceilDiv(ink.y + ink.height, 2) - top;
        if (w + 4 > SIDE || h + 4 > SIDE) return glyph(font, id, density * .5f);
        var image = new BufferedImage(2*w, 2*h, BufferedImage.TYPE_BYTE_GRAY);
        var painter = image.createGraphics();
        try {
            painter.setColor(Color.WHITE);
            painter.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            painter.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            painter.drawGlyphVector(vector, -left * 2, -top * 2);
        } finally {
            painter.dispose();
        }
        Page page = null;
        for (Page candidate : pages) {
            if (candidate.fit(w + 4, h + 4)) { page = candidate; break; }
        }
        if (page == null) {
            page = new Page();
            pages.add(page);
        }
        int u = page.x + 2, v = page.y + 2;
        var samples = image.getRaster();
        var pixels = Objects.requireNonNull(page.texture.getPixels());
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int a = samples.getSample(2*x, 2*y, 0) + samples.getSample(2*x+1, 2*y, 0)
                        + samples.getSample(2*x, 2*y+1, 0) + samples.getSample(2*x+1, 2*y+1, 0);
                pixels.setPixel(u + x, v + y, ((a + 2) / 4 << 24) | 0xffffff);
            }
        }
        page.x += w + 4;
        page.rowHeight = Math.max(page.rowHeight, h + 4);
        page.dirty = true;
        page.lastUsed = frame;
        // Include one transparent texel in geometry so fractional positions filter the outermost samples.
        var tile = new Tile(page, u-1, v-1, w+2, h+2, (left-1)/density, (top-1)/density, 1/density);
        tiles.put(key, tile);
        return tile;
    }

    public static void upload() {
        for (var page : pages) {
            if (page.dirty) { page.texture.upload(); page.dirty = false; }
        }
    }

    public static void endFrame() {
        frame++;
        if (pages.size() <= RETAINED_PAGES) return;
        pages.sort(Comparator.comparingLong(p -> p.lastUsed));
        var retired = new HashSet<>(pages.subList(0, pages.size() - RETAINED_PAGES));
        tiles.values().removeIf(t -> retired.contains(t.page));
        pages.removeAll(retired);
        retired.forEach(p -> p.texture.close());
    }

    public static void release() {
        pages.forEach(p -> p.texture.close());
        pages.clear();
        tiles.clear();
        GuiTextLayout.clear();
    }
}
