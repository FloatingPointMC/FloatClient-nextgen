package io.github.floatingpointmc.client.nextgen.mixin.impl;

import io.github.floatingpointmc.client.nextgen.util.render.BlurUtil;
import net.minecraft.client.renderer.GameRenderer;
import io.github.floatingpointmc.client.nextgen.ui.font.GlyphAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class BlurLifecycleMixin {
    @Inject(method = {"close", "onResourceManagerReload"}, at = @At("HEAD"))
    private void floatclient$releaseEffects(CallbackInfo ci) {
        GlyphAtlas.release();
        BlurUtil.release();
    }
}
