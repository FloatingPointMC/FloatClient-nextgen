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

import io.github.floatingpointmc.client.nextgen.mixin.interfaces.IGuiGraphicsExtractor;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayDeque;
import java.util.Deque;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin implements IGuiGraphicsExtractor {
    @Shadow
    @Final
    @Getter
    private GuiRenderState guiRenderState;

    @Shadow
    @Final
    private Matrix3x2fStack pose;

    @Unique
    private final Deque<ScreenRectangle> floatclient$scissors = new ArrayDeque<>();

    @Inject(method = "enableScissor", at = @At("HEAD"))
    private void floatclient$trackEnableScissor(int left, int top, int right, int bottom, CallbackInfo ci) {
        ScreenRectangle rectangle = new ScreenRectangle(left, top, right - left, bottom - top)
                .transformAxisAligned(pose);
        if (!floatclient$scissors.isEmpty()) {
            rectangle = floatclient$scissors.peek().intersection(rectangle);
        }
        floatclient$scissors.push(rectangle);
    }

    @Inject(method = "disableScissor", at = @At("HEAD"))
    private void floatclient$trackDisableScissor(CallbackInfo ci) {
        if (!floatclient$scissors.isEmpty()) floatclient$scissors.pop();
    }

    @Override
    public @Nullable ScreenRectangle floatclient$getScissor() {
        return floatclient$scissors.peek();
    }

}
