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

import io.github.floatingpointmc.client.nextgen.event.impl.player.PreMotionEvent;
import io.github.vlouboos.standaloneevent.api.StandaloneEventAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayerMixin {
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    protected abstract void sendIsSprintingIfNeeded();

    @Shadow
    protected abstract boolean isControlledCamera();

    @Shadow
    private double xLast;

    @Shadow
    private double yLast;

    @Shadow
    private double zLast;

    @Shadow
    private float yRotLast;

    @Shadow
    private float xRotLast;

    @Shadow
    private int positionReminder;

    @Shadow
    @Final
    public ClientPacketListener connection;

    @Shadow
    private boolean lastOnGround;

    @Shadow
    private boolean lastHorizontalCollision;

    @Shadow
    private boolean autoJumpEnabled;

    /**
     * @author FloatingPoint-MC
     * @reason Injecting {@code PreMotionEvent}
     */
    @Overwrite
    public void sendPosition() {
        this.sendIsSprintingIfNeeded();
        if (this.isControlledCamera()) {
            Vec3 position = position();
            PreMotionEvent event = new PreMotionEvent(position.x, position.y, position.z, getYRot(), getXRot(), onGround(), horizontalCollision);
            StandaloneEventAPI.getApi().call(event);
            if (event.canceled) return;
            Vec3 newPosition = new Vec3(event.x, event.y, event.z);
            double deltaX = event.x - xLast;
            double deltaY = event.y - yLast;
            double deltaZ = event.z - zLast;
            double deltaYRot = event.yaw - yRotLast;
            double deltaXRot = event.pitch - xRotLast;
            ++positionReminder;
            boolean move = Mth.lengthSquared(deltaX, deltaY, deltaZ) > Mth.square(2.0E-4) || positionReminder >= 20;
            boolean rot = deltaYRot != (double) 0.0F || deltaXRot != (double) 0.0F;
            if (move && rot) {
                connection.send(new ServerboundMovePlayerPacket.PosRot(newPosition, event.yaw, event.pitch, event.onGround, event.horizontalCollision));
            } else if (move) {
                connection.send(new ServerboundMovePlayerPacket.Pos(newPosition, event.onGround, event.horizontalCollision));
            } else if (rot) {
                connection.send(new ServerboundMovePlayerPacket.Rot(event.yaw, event.pitch, event.onGround, event.horizontalCollision));
            } else if (lastOnGround != event.onGround || lastHorizontalCollision != event.horizontalCollision) {
                connection.send(new ServerboundMovePlayerPacket.StatusOnly(event.onGround, event.horizontalCollision));
            }

            if (move) {
                xLast = event.x;
                yLast = event.y;
                zLast = event.z;
                positionReminder = 0;
            }

            if (rot) {
                yRotLast = event.yaw;
                xRotLast = event.pitch;
            }

            lastOnGround = event.onGround;
            lastHorizontalCollision = event.horizontalCollision;
            autoJumpEnabled = minecraft.options.autoJump().get();
        }
    }
}
