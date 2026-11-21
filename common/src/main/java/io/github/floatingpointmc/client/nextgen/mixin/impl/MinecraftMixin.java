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
import io.github.floatingpointmc.client.nextgen.FloatClientNextgen;
import io.github.floatingpointmc.client.nextgen.FloatMetadataResources;
import io.github.floatingpointmc.client.nextgen.event.impl.client.KeyEvent;
import io.github.floatingpointmc.client.nextgen.event.impl.client.TickEvent;
import io.github.floatingpointmc.client.nextgen.mixin.interfaces.IKeyMapping;
import io.github.floatingpointmc.client.nextgen.util.io.KeyboardUtil;
import io.github.vlouboos.standaloneevent.api.StandaloneEventAPI;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackMetadataResources;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        StandaloneEventAPI.getApi().call(new TickEvent());
    }

    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void onKey(CallbackInfo ci) {
        while (!KeyboardUtil.KEY_BUFFER.isEmpty()) {
            InputConstants.Key key = KeyboardUtil.KEY_BUFFER.poll();
            assert key != null;
            KeyEvent e = new KeyEvent(key.getValue());
            StandaloneEventAPI.getApi().call(e);
            if (e.canceled) {
                for (KeyMapping mapping : Minecraft.getInstance().options.keyMappings) {
                    if (mapping.matches(key)) {
                        ((IKeyMapping) mapping).floatclient_nextgen$clear();
                    }
                }
                ci.cancel();
            }
        }
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;setIcon(Lnet/minecraft/server/packs/PackMetadataResources;Lcom/mojang/blaze3d/platform/IconSet;)V"), index = 0)
    private PackMetadataResources onIcon(PackMetadataResources resources) {
        return new FloatMetadataResources();
    }

    @ModifyArg(method = "createTitle", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;<init>(Ljava/lang/String;)V"))
    private String onTitle(String str) {
        return "Float-Nextgen %s | %s".formatted(FloatClientNextgen.VERSION, str);
    }
}
