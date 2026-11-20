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

package io.github.floatingpointmc.client.nextgen.ui.blur;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.pipeline.*;
import com.mojang.renderpearl.api.textures.FilterMode;
import io.github.floatingpointmc.client.nextgen.ui.shader.Shader;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryStack;

import java.util.Optional;

/** Pass-local bindings for position_uv. Clip-space vertices use identity transforms. */
public final class FullscreenPass {
    private static final Matrix4f IDENTITY = new Matrix4f();
    static final Vector4fc TRANSPARENT = new Vector4f(0f);
    private static GpuBuffer triangle;
    private static GpuBuffer projection;
    private final String label;
    private final RenderPipeline pipeline;
    private final boolean masked;

    FullscreenPass(String label, ColorTargetState color, boolean masked) {
        this(label, label, color, masked);
    }

    FullscreenPass(String label, String fragment, ColorTargetState color, boolean masked) {
        this.label = label;
        this.masked = masked;
        var layout = Shader.transforms().withUniform("inTexture", UniformType.COMBINED_IMAGE_SAMPLER);
        layout.withUniform("Parameters", UniformType.UNIFORM_BUFFER);
        if (masked) layout.withUniform("textureToCheck", UniformType.COMBINED_IMAGE_SAMPLER);
        pipeline = Shader.positionUvPipeline(label, fragment, layout.build(), color);
    }

    void draw(RenderTarget input, RenderTarget output, RenderTarget mask, boolean linear,
              float x, float y, float z, float w) {
        if (input == output || mask == output) throw new IllegalArgumentException("Framebuffer feedback");
        var device = RenderSystem.getDevice();
        var encoder = device.createCommandEncoder();
        try (var stack = MemoryStack.stackPush()) {
            if (triangle == null) {
                var vertices = stack.malloc(3 * 20);
                vertices.asFloatBuffer().put(new float[]{-1,-1,0, 0,0, 3,-1,0, 2,0, -1,3,0, 0,2});
                triangle = device.createBuffer(() -> "Float fullscreen triangle", GpuBuffer.USAGE_VERTEX, vertices);
            }
            if (projection == null) {
                projection = device.createBuffer(() -> "Float identity projection", GpuBuffer.USAGE_UNIFORM,
                        IDENTITY.get(stack.malloc(64)));
            }
            var params = encoder.transientMemory().uploadGpu(
                    stack.malloc(16).putFloat(x).putFloat(y).putFloat(z).putFloat(w).flip(),
                    device.getDeviceInfo().limits().minUniformOffsetAlignment(), GpuBuffer.USAGE_UNIFORM);
            var transform = RenderSystem.getDynamicUniforms().writeTransform(IDENTITY);
            var compiled = RenderSystem.getCompiledPipeline(pipeline);
            try (var pass = encoder.createRenderPass(() -> label, output.getColorTextureView(),
                    Optional.empty())) {
                pass.setPipeline(compiled);
                pass.setUniform("Projection", projection);
                pass.setUniform("DynamicTransforms", transform);
                pass.setUniform("Parameters", params);
                var samplers = RenderSystem.getSamplerCache();
                pass.setUniform("inTexture", input.getColorTextureView(),
                        samplers.getClampToEdge(linear ? FilterMode.LINEAR : FilterMode.NEAREST));
                if (masked) pass.setUniform("textureToCheck", mask.getColorTextureView(), samplers.getClampToEdge(FilterMode.NEAREST));
                pass.setVertexBuffer(0, triangle.slice());
                pass.draw(3, 1, 0, 0);
            }
        }
    }

    public static void releaseGeometry() {
        if (triangle != null) triangle.close();
        if (projection != null) projection.close();
        triangle = null;
        projection = null;
    }
}
