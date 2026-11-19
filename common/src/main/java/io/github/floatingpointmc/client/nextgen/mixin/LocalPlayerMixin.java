package io.github.floatingpointmc.client.nextgen.mixin;

import io.github.floatingpointmc.client.nextgen.event.impl.player.PreMotionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayerMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;
    private double eventX, eventY, eventZ;
    private float eventYaw, eventPitch;
    private boolean eventOnGround;

    @Inject(method = "sendPosition", at = @At("HEAD"))
    public void onSendingPosition(CallbackInfo ci) {
        Vec3 position = position();
        PreMotionEvent event = new PreMotionEvent(position.x, position.y, position.z, getYRot(), getXRot(), onGround());
    }
}
