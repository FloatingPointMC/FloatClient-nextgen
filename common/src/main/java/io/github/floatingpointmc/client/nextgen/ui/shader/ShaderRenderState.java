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

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.DynamicGpuDataStorage;
import java.nio.ByteBuffer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/** Shared GUI state for a draw submitted through a custom shader pipeline. */
public record ShaderRenderState(@Nullable RenderPipeline pipeline, @NotNull TextureSetup textureSetup,
                                @Nullable ScreenRectangle scissorArea, @NotNull ScreenRectangle bounds,
                                @NotNull Consumer<VertexConsumer> geometry,
                                @Nullable Uniforms uniforms,
                                @Nullable RenderTarget target) implements GuiElementRenderState {
    @Override
    public void buildVertices(@NotNull VertexConsumer out) {
        geometry.accept(out);
    }

    public ShaderRenderState(RenderPipeline pipeline, TextureSetup textureSetup, ScreenRectangle scissorArea,
                             ScreenRectangle bounds, Consumer<VertexConsumer> geometry) {
        this(pipeline, textureSetup, scissorArea, bounds, geometry, null, null);
    }

    public ShaderRenderState(RenderPipeline pipeline, TextureSetup textureSetup, ScreenRectangle scissorArea,
                             ScreenRectangle bounds, Consumer<VertexConsumer> geometry, Uniforms uniforms) {
        this(pipeline, textureSetup, scissorArea, bounds, geometry, uniforms, null);
    }

    /** ARGB modulation and one std140 vec4 in the Parameters block. */
    public record Uniforms(int color, float x, float y, float z, float w)
            implements DynamicGpuDataStorage.DynamicGpuData {
        @Override
        public void write(ByteBuffer buffer) {
            buffer.putFloat(x).putFloat(y).putFloat(z).putFloat(w);
        }
    }
}
