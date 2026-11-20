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

import com.mojang.blaze3d.platform.InputConstants;
import io.github.floatingpointmc.client.nextgen.mixin.interfaces.IKeyMapping;
import io.github.floatingpointmc.client.nextgen.util.io.KeyboardUtil;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(KeyMapping.class)
public class KeyMappingMixin implements IKeyMapping {
    @Shadow
    private int clickCount;

    @Shadow
    private boolean isDown;

    @Inject(method = "click", at = @At("HEAD"))
    private static void onKey(InputConstants.Key key, CallbackInfo ci) {
        KeyboardUtil.KEY_BUFFER.add(key);
    }

    @Override
    public void floatclient_nextgen$clear() {
        clickCount = 0;
        isDown = false;
    }

    @Accessor
    public static Map<InputConstants.Key, List<KeyMapping>> getMAP() {
        throw new UnsupportedOperationException("Implemented via mixin");
    }
}
