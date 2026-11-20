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

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LocationUtil {
    public static @NotNull Vec3 closestVecTo(@NotNull Entity entity, @NotNull Vec3 from) {
        AABB aabb = entity.getBoundingBox();
        double x = Math.clamp(from.x, aabb.minX, aabb.maxX);
        double y = Math.clamp(from.y, aabb.minY, aabb.maxY);
        double z = Math.clamp(from.z, aabb.minZ, aabb.maxZ);
        return new Vec3(x, y, z);
    }

    public static double closestDistanceTo(@NotNull Entity entity, @NotNull Vec3 from) {
        return closestVecTo(entity, from).distanceTo(from);
    }
}
