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
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.joml.Vector3fc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
@UtilityClass
public class RenderUtil {
    private static final Shader ROUNDED_RECT;
    private static final Shader ROUNDED_OUTLINE;
    private static final Shader ROUNDED_RECT_GRADIENT;
    private static final Shader ROUNDED_RECT_GRADIENT_HORIZONTAL;
    private static final Shader CHEVRON;
    private static final ConcurrentLinkedQueue<WorldOperation> WORLD_OPERATIONS = new ConcurrentLinkedQueue<>();

    static {
        ROUNDED_RECT = new Shader("rounded_rect");
        ROUNDED_OUTLINE = new Shader("rounded_outline");
        ROUNDED_RECT_GRADIENT = new Shader("rounded_rect_gradient");
        ROUNDED_RECT_GRADIENT_HORIZONTAL = new Shader("rounded_rect_gradient_horizontal");
        CHEVRON = new Shader("chevron");
    }

    public static void init() {
        log.info("Loaded render utilities.");
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
        drawImage(graphics, location, x, y, width, height, -1);
    }

    public static void drawImage(@NotNull GuiGraphicsExtractor graphics, @NotNull Identifier location, int x, int y, int width, int height, int color) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, location, x, y, 0, 0, width, height, width, height, color);
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

    public static void drawBlock(@NotNull BlockPos blockPos, int lineColor, int fillColor) {
        drawBoundingBox(new AABB(blockPos), lineColor, fillColor);
    }

    public static void drawBoundingBox(@NotNull AABB boundingBox, int lineColor, int fillColor) {
        Vec3 a = new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        Vec3 b = new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        Vec3 c = new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        Vec3 d = new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        Vec3 e = new Vec3(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        Vec3 f = new Vec3(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        Vec3 g = new Vec3(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        Vec3 h = new Vec3(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);

        draw3DRect(a, b, c, d, fillColor);
        draw3DRect(e, h, g, f, fillColor);
        draw3DRect(a, e, f, b, fillColor);
        draw3DRect(d, c, g, h, fillColor);
        draw3DRect(a, d, h, e, fillColor);
        draw3DRect(b, f, g, c, fillColor);

        draw3DLine(a, b, lineColor);
        draw3DLine(b, c, lineColor);
        draw3DLine(c, d, lineColor);
        draw3DLine(d, a, lineColor);
        draw3DLine(e, f, lineColor);
        draw3DLine(f, g, lineColor);
        draw3DLine(g, h, lineColor);
        draw3DLine(h, e, lineColor);
        draw3DLine(a, e, lineColor);
        draw3DLine(b, f, lineColor);
        draw3DLine(c, g, lineColor);
        draw3DLine(d, h, lineColor);
    }

    public static void draw2DESPOnEntity(@NotNull Entity entity, int lineColor, int fillColor) {
        AABB box = entity.getBoundingBox();
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 interpolatedPosition = entity.getPosition(partialTick);
        Vec3 currentPosition = entity.position();

        double centerX = interpolatedPosition.x + (box.minX - currentPosition.x) + box.getXsize() * 0.5D;
        double centerY = interpolatedPosition.y + (box.minY - currentPosition.y) + box.getYsize() * 0.5D;
        double centerZ = interpolatedPosition.z + (box.minZ - currentPosition.z) + box.getZsize() * 0.5D;
        double halfWidth = Math.max(box.getXsize(), box.getZsize()) * 0.5D;
        double halfHeight = box.getYsize() * 0.5D;

        var camera = Minecraft.getInstance().gameRenderer.mainCamera();
        Vector3fc cameraLeft = camera.leftVector();
        Vector3fc cameraUp = camera.upVector();
        Vec3 horizontal = new Vec3(cameraLeft.x(), cameraLeft.y(), cameraLeft.z()).normalize();
        Vec3 verticalAxis = new Vec3(cameraUp.x(), cameraUp.y(), cameraUp.z()).normalize();

        Vec3 center = new Vec3(centerX, centerY, centerZ);
        Vec3 halfHorizontal = horizontal.scale(halfWidth);
        Vec3 vertical = verticalAxis.scale(halfHeight);
        Vec3 a = center.subtract(halfHorizontal).subtract(vertical);
        Vec3 b = center.add(halfHorizontal).subtract(vertical);
        Vec3 c = center.add(halfHorizontal).add(vertical);
        Vec3 d = center.subtract(halfHorizontal).add(vertical);
        draw3DRect(a, b, c, d, fillColor);
        draw3DLine(a, b, lineColor);
        draw3DLine(b, c, lineColor);
        draw3DLine(c, d, lineColor);
        draw3DLine(d, a, lineColor);
    }

    public static void draw3DRect(@NotNull Vec3 a, @NotNull Vec3 b, @NotNull Vec3 c, @NotNull Vec3 d, int color) {
        WORLD_OPERATIONS.add(new RectOperation(a, b, c, d, color));
    }

    public static void draw3DLine(@NotNull Vec3 start, @NotNull Vec3 end, int color) {
        WORLD_OPERATIONS.add(new LineOperation(start, end, color));
    }

    /** Called from LevelRenderer while its render-thread GizmoCollector is active. */
    public static void flushWorldOperations(@NotNull LevelRenderer levelRenderer) {
        if (WORLD_OPERATIONS.isEmpty()) return;
        try (var ignored = levelRenderer.collectPerFrameRenderThreadGizmos()) {
            WorldOperation operation;
            while ((operation = WORLD_OPERATIONS.poll()) != null) operation.submit();
        }
    }

    private interface WorldOperation {
        void submit();
    }

    private record RectOperation(Vec3 a, Vec3 b, Vec3 c, Vec3 d, int color) implements WorldOperation {
        @Override
        public void submit() {
            Gizmos.rect(a, b, c, d, GizmoStyle.fill(color));
        }
    }

    private record LineOperation(Vec3 start, Vec3 end, int color) implements WorldOperation {
        @Override
        public void submit() {
            Gizmos.line(start, end, color);
        }
    }
}
