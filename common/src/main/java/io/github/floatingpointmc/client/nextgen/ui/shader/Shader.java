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

package io.github.floatingpointmc.client.nextgen.ui.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

import java.util.Optional;

/**
 * One sampled image and four float parameters. Use on the render thread between render passes.
 */
public final class Shader {
    private final RenderPipeline pipeline;

    public Shader(@NotNull String name) {
        pipeline = positionUvPipeline(name, transforms()
                .withUniform("Parameters", UniformType.UNIFORM_BUFFER).build(),
                new ColorTargetState(BlendFunction.TRANSLUCENT));
    }

    public static BindGroupLayout.Builder transforms() {
        return BindGroupLayout.builder()
                .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
                .withUniform("Projection", UniformType.UNIFORM_BUFFER);
    }

    public static RenderPipeline positionUvPipeline(String name, BindGroupLayout bindings, ColorTargetState color) {
        return RenderPipeline.builder()
                .withLocation(Identifier.fromNamespaceAndPath("floatclient_nextgen", name))
                .withVertexShader(Identifier.fromNamespaceAndPath("floatclient_nextgen", "position_uv"))
                .withFragmentShader(Identifier.fromNamespaceAndPath("floatclient_nextgen", name))
                .withBindGroupLayout(bindings)
                .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
                .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                .withDepthStencilState(Optional.empty())
                .withCull(false)
                .withColorTargetState(color)
                .build();
    }

    public @NotNull ShaderRenderState draw(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x2, float y2, @Nullable ScreenRectangle scissor, @NotNull ScreenRectangle bounds) {
        Matrix3x2fc snapshot = new Matrix3x2f(graphics.pose());
        return new ShaderRenderState(pipeline, TextureSetup.noTexture(), scissor, bounds, out -> {
            vertex(out, snapshot, x, y, 0, 0);
            vertex(out, snapshot, x, y2, 0, 1);
            vertex(out, snapshot, x2, y2, 1, 1);
            vertex(out, snapshot, x, y, 0, 0);
            vertex(out, snapshot, x2, y2, 1, 1);
            vertex(out, snapshot, x2, y, 1, 0);
        });
    }

    public @NotNull ShaderRenderState draw(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x2, float y2,
                                           int color, float first,
                                           @Nullable ScreenRectangle scissor, @NotNull ScreenRectangle bounds, @Nullable RenderTarget target) {
        return draw(graphics, x, y, x2, y2, color, first, 0, scissor, bounds, target);
    }

    public @NotNull ShaderRenderState draw(@NotNull GuiGraphicsExtractor graphics, float x, float y, float x2, float y2,
                                           int color, float first, float second,
                                           @Nullable ScreenRectangle scissor, @NotNull ScreenRectangle bounds,
                                           @Nullable RenderTarget target) {
        Matrix3x2fc snapshot = new Matrix3x2f(graphics.pose());
        return new ShaderRenderState(pipeline, TextureSetup.noTexture(), scissor, bounds, out -> {
            vertex(out, snapshot, x, y, 0, 0);
            vertex(out, snapshot, x, y2, 0, 1);
            vertex(out, snapshot, x2, y2, 1, 1);
            vertex(out, snapshot, x, y, 0, 0);
            vertex(out, snapshot, x2, y2, 1, 1);
            vertex(out, snapshot, x2, y, 1, 0);
        }, new ShaderRenderState.Uniforms(color, first, second, 0, 0), target);
    }

    private static void vertex(VertexConsumer out, Matrix3x2fc pose, float x, float y,
                               float u, float v) {
        out.addVertexWith2DPose(pose, x, y).setUv(u, v);
    }
}
