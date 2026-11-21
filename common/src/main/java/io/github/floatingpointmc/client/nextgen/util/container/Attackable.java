package io.github.floatingpointmc.client.nextgen.util.container;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record Attackable(@NotNull Entity target, float health, @NotNull Vec3 hitVec) {
}
