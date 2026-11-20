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

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import io.github.floatingpointmc.client.nextgen.ui.shader.Shader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Shader roundedShader = new Shader("rounded_rect");
    private static final Shader roundedGradientShader = new Shader("rounded_rect_gradient");
    private static final Shader roundedHGradientShader = new Shader("rounded_rect_gradient_horizontal");
    private static final Shader roundedOutlineShader = new Shader("rounded_outline");

    public static void drawRoundedRect(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x2, float y2, float radius, int c) {
        float width = x2 - x;
        float height = y2 - y;
        Color color = new Color(c, true);
        glColor4f(1, 1, 1, 1);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_ALPHA_TEST);
        glAlphaFunc(GL_GREATER, 0);
        roundedShader.init();

        setupRoundedRectUniforms(width, height, radius, roundedShader);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        Shader.drawQuads(x, y, width, height);
        roundedShader.unload();
        glDisable(GL_BLEND);
    }

    public static void setupRoundedRectUniforms(float width, float height, float radius, @NotNull Shader shader) {
        shader.setUniformf("rectSize", width * mc.getWindow().getGuiScale(), height * mc.getWindow().getGuiScale());
        shader.setUniformf("radius", radius * mc.getWindow().getGuiScale());
    }

    class RoundedRectRenderState implements GuiElementRenderState {

        @Override
        public void buildVertices(VertexConsumer vertexConsumer) {

        }

        @Override
        public RenderPipeline pipeline() {
            return null;
        }

        @Override
        public TextureSetup textureSetup() {
            return null;
        }

        @Override
        public @Nullable ScreenRectangle scissorArea() {
            return null;
        }

        @Override
        public @Nullable ScreenRectangle bounds() {
            return null;
        }
    }
}
