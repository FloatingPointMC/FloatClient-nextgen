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
import io.github.floatingpointmc.client.nextgen.ui.shader.state.impl.ShaderRenderState;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class RenderUtil {
    private static final Shader ROUNDED_RECT;
    private static final Shader ROUNDED_OUTLINE;
    private static final Shader ROUNDED_RECT_GRADIENT;
    private static final Shader ROUNDED_RECT_GRADIENT_HORIZONTAL;
    private static final Shader CHEVRON;

    static {
        ROUNDED_RECT = new Shader("rounded_rect");
        ROUNDED_OUTLINE = new Shader("rounded_outline");
        ROUNDED_RECT_GRADIENT = new Shader("rounded_rect_gradient");
        ROUNDED_RECT_GRADIENT_HORIZONTAL = new Shader("rounded_rect_gradient_horizontal");
        CHEVRON = new Shader("chevron");
    }

    public static void drawRoundedRect(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x1, float y1, float radius, int color) {
        var state = roundedRect(graphics, x, y, x1, y1, radius, color);
        if (state != null) state.submit(graphics);
    }

    public static @Nullable ShaderRenderState roundedRect(GuiGraphicsExtractor graphics, float x, float y, float x1, float y1, float radius, int color) {
        float width = x1 - x;
        float height = y1 - y;
        if (width <= 0 || height <= 0) return null;
        float fraction = Math.clamp(radius / Math.min(width, height), 0f, 0.5f);
        int left = (int) Math.floor(x);
        int top = (int) Math.floor(y);
        ScreenRectangle bounds = new ScreenRectangle(left, top,
                (int) Math.ceil(x + width) - left, (int) Math.ceil(y + height) - top).transformMaxBounds(graphics.pose());
        ScreenRectangle scissor = ((IGuiGraphicsExtractor) graphics).floatclient$getScissor();
        if (scissor != null) bounds = bounds.intersection(scissor);
        if (bounds == null || bounds.width() <= 0 || bounds.height() <= 0) return null;
        return ROUNDED_RECT.draw(graphics, x, y, x1, y1, scissor, bounds,
                (color >>> 16 & 0xFF) / 255f,
                (color >>> 8 & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                (color >>> 24 & 0xFF) / 255f,
                fraction);
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
        ROUNDED_OUTLINE.draw(graphics, x, y, x2, y2, scissor, bounds,
                (c >>> 16 & 0xFF) / 255f,
                (c >>> 8 & 0xFF) / 255f,
                (c & 0xFF) / 255f,
                (c >>> 24 & 0xFF) / 255f,
                radiusFraction, thicknessFraction).submit(graphics);
    }

    public static void drawRoundedGradientRect(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x1, float y1, float radius, int topColor, int bottomColor) {
        drawRoundedGradientRect(graphics, x, y, x1, y1, radius, y1 - y, topColor, bottomColor);
    }

    public static void drawRoundedGradientRect(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x1, float y1, float radius, float y2, int topColor, int bottomColor) {
        float width = x1 - x;
        float height = y1 - y;
        if (width <= 0 || height <= 0) return;
        float cornerRadius = Math.clamp(radius, 0f, 0.5f * Math.min(width, height));
        ScreenRectangle bounds = new ScreenRectangle((int) x, (int) y,
                (int) (Math.ceil(x + width) - x), (int) (Math.ceil(y + height) - y)).transformMaxBounds(graphics.pose());
        ScreenRectangle scissor = ((IGuiGraphicsExtractor) graphics).floatclient$getScissor();
        if (scissor != null) bounds = bounds.intersection(scissor);
        if (bounds == null || bounds.width() <= 0 || bounds.height() <= 0) return;
        ROUNDED_RECT_GRADIENT.draw(graphics, x, y, x1, y1, scissor, bounds,
                width, height, cornerRadius, y2, topColor, bottomColor).submit(graphics);
    }

    public static void drawRoundedGradientHorizontal(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x1, int y1, int radius, int leftColor, int rightColor) {
        float width = x1 - x;
        float height = y1 - y;
        if (width <= 0 || height <= 0) return;
        float cornerRadius = Math.clamp(radius, 0f, 0.5f * Math.min(width, height));
        ScreenRectangle bounds = new ScreenRectangle((int) x, (int) y,
                (int) (Math.ceil(x + width) - x), (int) (Math.ceil(y + height) - y)).transformMaxBounds(graphics.pose());
        ScreenRectangle scissor = ((IGuiGraphicsExtractor) graphics).floatclient$getScissor();
        if (scissor != null) bounds = bounds.intersection(scissor);
        if (bounds == null || bounds.width() <= 0 || bounds.height() <= 0) return;
        ROUNDED_RECT_GRADIENT_HORIZONTAL.draw(graphics, x, y, x1, y1, scissor, bounds,
                width, height, cornerRadius, leftColor, rightColor).submit(graphics);
    }

    public static void drawImage(@NotNull GuiGraphicsExtractor graphics, @NotNull Identifier location, int x, int y, int width, int height) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, location, x, y, 0, 0, width, height, width, height);
    }

    public static void drawChevron(@NotNull GuiGraphicsExtractor graphics, float centerX, float centerY,
                                   float angle, int color) {
        final float size = 5f;
        final float half = size * 0.5f;
        final float left = centerX - half;
        final float top = centerY - half;
        ScreenRectangle bounds = new ScreenRectangle((int) Math.floor(left), (int) Math.floor(top),
                (int) Math.ceil(size), (int) Math.ceil(size)).transformMaxBounds(graphics.pose());
        ScreenRectangle scissor = ((IGuiGraphicsExtractor) graphics).floatclient$getScissor();
        if (scissor != null) bounds = bounds.intersection(scissor);
        if (bounds == null || bounds.width() <= 0 || bounds.height() <= 0 || (color >>> 24) == 0) return;
        CHEVRON.draw(graphics, left, top, left + size, top + size, scissor, bounds,
                (color >>> 16 & 0xFF) / 255f,
                (color >>> 8 & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                (color >>> 24 & 0xFF) / 255f,
                angle, 0.5f, size, size).submit(graphics);
    }
}
