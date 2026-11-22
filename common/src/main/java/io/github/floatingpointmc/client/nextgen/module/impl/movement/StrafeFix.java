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

package io.github.floatingpointmc.client.nextgen.module.impl.movement;

import io.github.floatingpointmc.client.nextgen.event.impl.entity.EntityTickedEvent;
import io.github.floatingpointmc.client.nextgen.event.impl.entity.JumpEvent;
import io.github.floatingpointmc.client.nextgen.event.impl.entity.MoveEvent;
import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.util.math.RotationUtil;
import io.github.vlouboos.standaloneevent.api.EventHandler;

public class StrafeFix extends Module {
    private float yaw;

    public StrafeFix() {
        super("strafefix", Category.MOVEMENT);
    }

    @EventHandler(-2) // Ensure we have the latest data
    public void onLivingUpdate(EntityTickedEvent e) {
        yaw = RotationUtil.getCurrentRotation().yaw();
    }

    @EventHandler(-2) // Ensure we have the latest data
    public void onJump(JumpEvent e) {
        e.yaw = yaw;
    }

    @EventHandler(-2) // Ensure we have the latest data
    public void onMoveFlying(MoveEvent e) {
        e.yaw = yaw;
    }
}
