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

package io.github.floatingpointmc.client.nextgen.module.impl.misc;

import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.CheckBox;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;

public class
Target extends Module {
    private static final CheckBox player = new CheckBox("player", true);
    private static final CheckBox invisible = new CheckBox("invisible", true, player::getValue);
    private static final CheckBox mob = new CheckBox("mobs", true);
    private static final CheckBox animal = new CheckBox("animal", true);
    private static final CheckBox villager = new CheckBox("villager", true);
    private static final CheckBox fireball = new CheckBox("fireball", true);

    public Target() {
        super("target", Category.MISC, false);
        addArguments(player, invisible, mob, animal, villager, fireball);
    }

    public static boolean notQualified(Entity entity) {
        if (entity == Minecraft.getInstance().player) return true;
        if (entity instanceof LivingEntity living) {
            if (!living.isAlive()) return true;
            if (living.getActiveEffects().stream().anyMatch(e -> e.is(MobEffects.INVISIBILITY)) && !invisible.getValue()) return true;
            if (living instanceof Player) return !player.getValue();
            if (living instanceof Mob) return !mob.getValue();
            if (living instanceof Animal) return !animal.getValue();
            if (living instanceof Villager) return !villager.getValue();
        }
        if (entity instanceof Fireball) return !fireball.getValue();
        return true;
    }
}
