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

import io.github.floatingpointmc.client.nextgen.ui.shader.state.impl.TextRenderState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/** A reusable fonts face backed by FloatClient's cached glyph renderer. */
public final class Font {
    private final Identifier face;

    public Font(@NotNull Identifier face) {
        this.face = face;
    }

    public void drawString(GuiGraphicsExtractor graphics, String text, float size,
                           float x, float y, boolean shadow) {
        drawString(graphics, text, size, x, y, 0xFFFFFFFF, shadow);
    }

    public void drawString(GuiGraphicsExtractor graphics, String text, float size,
                           float x, float y, int color) {
        text(graphics, text, size, x, y, color, false).submit(graphics);
    }

    public void drawString(GuiGraphicsExtractor graphics, String text, float size,
                           float x, float y, int color, boolean shadow) {
        text(graphics, text, size, x, y, color, shadow).submit(graphics);
    }

    /** Creates a deferred operation for use as a Blur/Bloom capture input. */
    public TextRenderState text(GuiGraphicsExtractor graphics, String text, float size,
                                float x, float y, int color, boolean shadow) {
        return new TextRenderState(graphics, GuiTextLayout.measure(text, face, toLayoutSize(size)),
                x, y, color, shadow);
    }

    public float width(String text, float size) {
        return GuiTextLayout.measure(text, face, toLayoutSize(size)).width();
    }

    public float height(String text, float size) {
        return GuiTextLayout.measure(text, face, toLayoutSize(size)).height();
    }

    private static float toLayoutSize(float size) {
        if (!Float.isFinite(size) || size <= 0) throw new IllegalArgumentException("Expected a positive fonts size");
        // FloatClient's UI fonts sizes use half-pixel units (8.4 is approximately 16 px).
        return size * 2f;
    }
}
