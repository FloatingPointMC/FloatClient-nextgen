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

package io.github.floatingpointmc.client.nextgen.ui.shader.state.impl;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import io.github.floatingpointmc.client.nextgen.ui.shader.state.OperationRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.DynamicGpuDataStorage;

import java.nio.ByteBuffer;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Shared GUI state for a draw submitted through a custom shader pipeline.
 */
public final class ShaderRenderState extends OperationRenderState implements GuiElementRenderState {
    private final RenderPipeline pipeline;
    private final TextureSetup textureSetup;
    private final ScreenRectangle scissorArea;
    private final ScreenRectangle bounds;
    private final Consumer<VertexConsumer> geometry;
    private final Uniforms uniforms;

    public ShaderRenderState(RenderPipeline pipeline, TextureSetup textureSetup, ScreenRectangle scissorArea,
                             ScreenRectangle bounds, Consumer<VertexConsumer> geometry) {
        this(pipeline, textureSetup, scissorArea, bounds, geometry, null);
    }

    public ShaderRenderState(RenderPipeline pipeline, TextureSetup textureSetup, ScreenRectangle scissorArea,
                             ScreenRectangle bounds, Consumer<VertexConsumer> geometry, Uniforms uniforms) {
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
        this.geometry = geometry;
        this.uniforms = uniforms;
    }

    @Override public void submit(GuiRenderState state) { state.addGuiElement(this); }
    @Override public @NotNull RenderPipeline pipeline() { return pipeline; }
    @Override public @NotNull TextureSetup textureSetup() { return textureSetup; }
    @Override public ScreenRectangle scissorArea() { return scissorArea; }
    @Override public ScreenRectangle bounds() { return bounds; }
    @Override public void buildVertices(@NotNull VertexConsumer out) { geometry.accept(out); }
    public @Nullable Uniforms uniforms() { return uniforms; }

    public interface Uniform extends DynamicGpuDataStorage.DynamicGpuData {
    }

    public record UniformI(int value) implements Uniform {
        @Override
        public void write(@NotNull ByteBuffer byteBuffer) {
            byteBuffer.putInt(value);
        }
    }

    public record UniformF(float value) implements Uniform {
        @Override
        public void write(@NotNull ByteBuffer byteBuffer) {
            byteBuffer.putFloat(value);
        }
    }

    public record Uniforms(@NotNull Uniform @NotNull ... uniforms) implements DynamicGpuDataStorage.DynamicGpuData {
        @Override
        public void write(@NotNull ByteBuffer byteBuffer) {
            for (Uniform uniform : uniforms) {
                uniform.write(byteBuffer);
            }
        }
    }
}
