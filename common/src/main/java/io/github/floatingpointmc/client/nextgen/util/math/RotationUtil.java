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
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
@UtilityClass
public class RotationUtil {
    private static final Minecraft mc = Minecraft.getInstance();
    private static @Nullable Rotation currentRotation, prevRotation;
    @Getter
    private static boolean active;

    static {
        StandaloneEventAPI.getApi().register(RotationUtil.class);
    }

    public static void init() {
        log.info("Loaded rotation utilities.");
    }

    @EventHandler(Long.MAX_VALUE)
    public static void onTick(TickEvent event) {
        if (mc.player == null) return;
        active = false;
        Rotation current = new Rotation(mc.player.getYRot(), mc.player.getXRot());
        if (prevRotation == null) {
            prevRotation = current;
            return;
        }
        Rotation offset = current.offsetYaw(prevRotation);
        currentRotation = getRotationWithOffset(offset);
    }

    @EventHandler(-1)
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

    private static @NotNull Rotation getPrevRotation() {
        if (prevRotation != null) return prevRotation;
        return new Rotation(0, 0);
    }

    public static void setCurrentRotation(@NotNull Rotation rotation) {
        active = true;
        currentRotation = rotation;
    }

    public static @NotNull Rotation getRotationWithOffset(@NotNull Rotation offset) {
        Rotation current = getPrevRotation();
        return new Rotation(current.yaw() + offset.yaw(), offset.pitch());
    }

    public static @NotNull Rotation getRotationOffsetToVec(@NotNull Vec3 vec) {
        assert mc.player != null;
        Vec3 self = mc.player.getEyePosition();
        double xDist = vec.x - self.x;
        double yDist = vec.y - self.y;
        double zDist = vec.z - self.z;
        float yaw = (float) (Math.atan2(zDist, xDist) * Mth.RAD_TO_DEG - 90.0);
        float pitch = (float) -(Math.atan2(Math.hypot(xDist, zDist), yDist) * Mth.RAD_TO_DEG);
        return new Rotation(yaw, pitch).offsetYaw(getPrevRotation());
    }

    public static @NotNull Rotation wrap(@NotNull Rotation offset, float rotationSpeed) {
        if (offset.yaw() > rotationSpeed) return new Rotation(rotationSpeed, offset.pitch());
        if (offset.yaw() < -rotationSpeed) return new Rotation(-rotationSpeed, offset.pitch());
        return offset;
    }
}
