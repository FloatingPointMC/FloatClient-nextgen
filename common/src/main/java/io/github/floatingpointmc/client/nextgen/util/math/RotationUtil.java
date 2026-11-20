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

package io.github.floatingpointmc.client.nextgen.util.math;

import io.github.floatingpointmc.client.nextgen.event.impl.client.TickEvent;
import io.github.floatingpointmc.client.nextgen.event.impl.player.PreMotionEvent;
import io.github.floatingpointmc.client.nextgen.util.container.Rotation;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import io.github.vlouboos.standaloneevent.api.StandaloneEventAPI;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class RotationUtil {
    private static final Minecraft mc = Minecraft.getInstance();
    private static @Nullable Rotation currentRotation, prevRotation;

    static {
        StandaloneEventAPI.getApi().register(RotationUtil.class);
    }

    @EventHandler(Long.MAX_VALUE)
    public static void onTick(TickEvent event) {
        if (mc.player == null) return;
        currentRotation = new Rotation(mc.player.getYRot(), mc.player.getXRot());
    }

    @EventHandler(Long.MIN_VALUE)
    public static void onPreMotion(PreMotionEvent e) {
        assert currentRotation != null;
        e.yaw = currentRotation.yaw();
        e.pitch = currentRotation.pitch();
        prevRotation = currentRotation;
        currentRotation = null;
    }

    public static @NotNull Rotation getCurrentRotation() {
        if (currentRotation != null) return currentRotation;
        if (prevRotation != null) return prevRotation;
        return new Rotation(0, 0);
    }

    public static void setCurrentRotation(@NotNull Rotation rotation) {
        currentRotation = rotation;
    }

    public static @NotNull Rotation getRotationOffset(@NotNull LivingEntity target) {
        assert mc.player != null;
        Vec3 closest = LocationUtil.closestVecTo(target, mc.player.getEyePosition());
        Rotation rotation = getRotationOffsetToVec(closest);
        Rotation current = getCurrentRotation();
        return new Rotation(current.yaw() + rotation.yaw(), rotation.pitch());
    }

    private static @NotNull Rotation getRotationOffsetToVec(@NotNull Vec3 vec) {
        assert mc.player != null;
        Vec3 self = mc.player.getEyePosition();
        double xDist = vec.x - self.x;
        double yDist = vec.y - self.y;
        double zDist = vec.z - self.z;
        float yaw = (float) (Math.atan2(zDist, xDist) * Mth.RAD_TO_DEG - 90.0);
        float pitch = (float) -(Math.atan2(Math.hypot(xDist, zDist), yDist) * Mth.RAD_TO_DEG);
        return new Rotation(yaw, pitch).offsetYaw(getCurrentRotation());
    }
}
