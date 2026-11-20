package io.github.floatingpointmc.client.nextgen.util.container;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record Attackable(@NotNull LivingEntity target, @NotNull Vec3 hitVec) {
}
