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

package io.github.floatingpointmc.client.nextgen.mixin.impl;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import io.github.floatingpointmc.client.nextgen.ui.shader.ShaderRenderState;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.DynamicGpuDataStorageMapped;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {
    @Shadow @Final private List<?> draws;
    @Shadow private RenderPipeline previousPipeline;
    @Unique private ShaderRenderState.Uniforms floatclient$previous;
    @Unique private ShaderRenderState.Operation floatclient$previousTarget;
    @Unique private final Map<Object, ShaderRenderState.Uniforms> floatclient$styles = new IdentityHashMap<>();
    @Unique private final Map<Object, GpuBufferSlice> floatclient$colors = new IdentityHashMap<>();
    @Unique private final Map<Object, GpuBufferSlice> floatclient$params = new IdentityHashMap<>();
    @Unique private final Map<Object, ShaderRenderState.Operation> floatclient$targets = new IdentityHashMap<>();
    @Unique private DynamicGpuDataStorageMapped<ShaderRenderState.Uniforms> floatclient$storage;
    @Unique private GpuBufferSlice floatclient$defaultTransform;

    @Inject(method = "addElementToMesh", at = @At("HEAD"))
    private void floatclient$checkBatch(GuiElementRenderState element, CallbackInfo ci) {
        var style = element instanceof ShaderRenderState shader ? shader.uniforms() : null;
        var target = element instanceof ShaderRenderState shader ? shader.operation() : null;
        if (!Objects.equals(style, floatclient$previous) || target != floatclient$previousTarget) previousPipeline = null;
        floatclient$previous = style;
        floatclient$previousTarget = target;
    }

    @Inject(method = "addElementToMesh", at = @At("RETURN"))
    private void floatclient$recordBatch(GuiElementRenderState element, CallbackInfo ci) {
        if (floatclient$previous != null) floatclient$styles.put(draws.getLast(), floatclient$previous);
        if (element instanceof ShaderRenderState shader && shader.operation() != null) {
            floatclient$targets.put(draws.getLast(), shader.operation());
        }
    }

    @Shadow
    private void executeDrawRange(Supplier<String> label, RenderTarget target, GpuBufferSlice transform, int first, int end) {
        throw new AssertionError();
    }

    @Redirect(method = "draw", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/render/GuiRenderer;executeDrawRange(Ljava/util/function/Supplier;Lcom/mojang/blaze3d/pipeline/RenderTarget;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;II)V"))
    private void floatclient$drawRanges(GuiRenderer renderer, Supplier<String> label, RenderTarget main,
                                        GpuBufferSlice transform, int first, int end) {
        if (floatclient$targets.isEmpty()) {
            executeDrawRange(label, main, transform, first, end);
            return;
        }
        while (first < end) {
            var operation = floatclient$targets.get(draws.get(first));
            int start = first++;
            while (first < end && floatclient$targets.get(draws.get(first)) == operation) first++;
            int stop = first;
            if (operation == null) executeDrawRange(label, main, transform, start, stop);
            else operation.render(main, target -> executeDrawRange(label, target, transform, start, stop));
        }
    }

    // Vanilla supplies the GUI view matrix here, before either GUI render pass opens.
    @ModifyArg(method = "draw", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DynamicGpuData;writeTransform(Lorg/joml/Matrix4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;"))
    private Matrix4f floatclient$uploadStyles(Matrix4f matrix) {
        if (floatclient$styles.isEmpty()) return matrix;
        if (floatclient$storage == null) {
            floatclient$storage = new DynamicGpuDataStorageMapped<>("GUI shader parameters", 16, GpuBuffer.USAGE_UNIFORM, 16);
        }
        for (var entry : floatclient$styles.entrySet()) {
            var style = entry.getValue();
            floatclient$colors.put(entry.getKey(), RenderSystem.getDynamicUniforms()
                    .writeTransform(matrix, ARGB.vector4fFromARGB32(style.color())));
            floatclient$params.put(entry.getKey(), floatclient$storage.writeData(style));
        }
        return matrix;
    }

    @Inject(method = "executeDrawRange", at = @At("HEAD"))
    private void floatclient$captureTransform(Supplier<String> label, RenderTarget target, GpuBufferSlice transform,
                                              int first, int end, CallbackInfo ci) {
        floatclient$defaultTransform = transform;
    }

    @Inject(method = "executeDraw", at = @At(value = "INVOKE",
            target = "Lcom/mojang/renderpearl/api/commands/RenderPass;drawIndexed(IIIII)V"))
    private void floatclient$bindStyle(@Coerce Object draw, RenderPass pass, CallbackInfo ci) {
        var color = floatclient$colors.get(draw);
        if (color != null) {
            pass.setUniform("DynamicTransforms", color);
            pass.setUniform("Parameters", floatclient$params.get(draw));
        }
    }

    @Inject(method = "executeDraw", at = @At(value = "INVOKE",
            target = "Lcom/mojang/renderpearl/api/commands/RenderPass;drawIndexed(IIIII)V", shift = At.Shift.AFTER))
    private void floatclient$restoreTransform(@Coerce Object draw, RenderPass pass, CallbackInfo ci) {
        if (floatclient$colors.containsKey(draw)) pass.setUniform("DynamicTransforms", floatclient$defaultTransform);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void floatclient$finishFrame(CallbackInfo ci) {
        floatclient$styles.clear();
        floatclient$colors.clear();
        floatclient$params.clear();
        floatclient$targets.clear();
        floatclient$previous = null;
        floatclient$previousTarget = null;
        floatclient$defaultTransform = null;
        if (floatclient$storage != null) floatclient$storage.endFrame();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void floatclient$closeStorage(CallbackInfo ci) {
        if (floatclient$storage != null) floatclient$storage.close();
    }
}
