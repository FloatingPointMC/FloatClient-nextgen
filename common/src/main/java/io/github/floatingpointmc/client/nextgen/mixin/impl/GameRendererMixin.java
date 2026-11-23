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

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.floatingpointmc.client.nextgen.event.impl.render.Render3DEvent;
import io.github.floatingpointmc.client.nextgen.module.ModuleRegistry;
import io.github.vlouboos.standaloneevent.api.StandaloneEventAPI;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.OptionsRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render3dHud", at = @At("HEAD"))
    private void onRender3D(CameraRenderState cameraState, PlayerRenderState playerState, OptionsRenderState optionsState, boolean consistentDepthRequired, CallbackInfo ci) {
        StandaloneEventAPI.getApi().call(new Render3DEvent());
    }

    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo ci) {
        if (ModuleRegistry.get("nohurtcam").orElseThrow().isEnabled()) ci.cancel();
    }
}
