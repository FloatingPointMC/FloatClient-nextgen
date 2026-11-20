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

package io.github.floatingpointmc.client.nextgen.util.render;

import io.github.floatingpointmc.client.nextgen.mixin.interfaces.IGuiGraphicsExtractor;
import io.github.floatingpointmc.client.nextgen.ui.shader.Shader;
import lombok.experimental.UtilityClass;
import io.github.floatingpointmc.client.nextgen.ui.shader.ShaderRenderState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class RenderUtil {
    private static final Shader ROUNDED_RECT;
    private static final Shader ROUNDED_OUTLINE;

    static {
        ROUNDED_RECT = new Shader("rounded_rect");
        ROUNDED_OUTLINE = new Shader("rounded_outline");
    }

    public static void drawRoundedRect(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x2, float y2, float radius, int color) {
        var state = roundedRect(graphics, x, y, x2, y2, radius, color);
        if (state != null) ((IGuiGraphicsExtractor) graphics).getGuiRenderState().addGuiElement(state);
    }

    static @Nullable ShaderRenderState roundedRect(GuiGraphicsExtractor graphics, float x, float y, float x2, float y2, float radius, int color) {
        float width = x2 - x;
        float height = y2 - y;
        if (width <= 0 || height <= 0) return null;
        float fraction = Math.clamp(radius / Math.min(width, height), 0f, 0.5f);
        int left = (int) Math.floor(x);
        int top = (int) Math.floor(y);
        ScreenRectangle bounds = new ScreenRectangle(left, top,
                (int) Math.ceil(x + width) - left, (int) Math.ceil(y + height) - top).transformMaxBounds(graphics.pose());
        ScreenRectangle scissor = ((IGuiGraphicsExtractor) graphics).floatclient$getScissor();
        if (scissor != null) bounds = bounds.intersection(scissor);
        if (bounds == null || bounds.width() <= 0 || bounds.height() <= 0) return null;
        return ROUNDED_RECT.draw(graphics, x, y, x2, y2, color, fraction, scissor, bounds);
    }

    public static void drawRoundedOutline(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x2, float y2, float radius, float thickness, int c) {
        float width = x2 - x;
        float height = y2 - y;
        if (width <= 0 || height <= 0 || thickness <= 0) return;
        float scale = Math.min(width, height);
        float radiusFraction = Math.clamp(radius / scale, 0f, 0.5f);
        float thicknessFraction = Math.clamp(thickness / scale, 0f, 1f);
        int left = (int) Math.floor(x);
        int top = (int) Math.floor(y);
        ScreenRectangle bounds = new ScreenRectangle(left, top,
                (int) Math.ceil(x + width) - left, (int) Math.ceil(y + height) - top).transformMaxBounds(graphics.pose());
        ScreenRectangle scissor = ((IGuiGraphicsExtractor) graphics).floatclient$getScissor();
        if (scissor != null) bounds = bounds.intersection(scissor);
        if (bounds == null || bounds.width() <= 0 || bounds.height() <= 0) return;
        ((IGuiGraphicsExtractor) graphics).getGuiRenderState()
                .addGuiElement(ROUNDED_OUTLINE.draw(graphics, x, y, x2, y2, c,
                        radiusFraction, thicknessFraction, scissor, bounds));
    }
}
