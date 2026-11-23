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

import io.github.floatingpointmc.client.nextgen.event.impl.client.KeyboardInputEvent;
import io.github.floatingpointmc.client.nextgen.event.impl.entity.EntityTickedEvent;
import io.github.floatingpointmc.client.nextgen.event.impl.entity.JumpEvent;
import io.github.floatingpointmc.client.nextgen.event.impl.entity.MoveEvent;
import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.CheckBox;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import io.github.floatingpointmc.client.nextgen.util.math.RotationUtil;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;

public class StrafeFix extends Module {
    private final CheckBox adapt = new CheckBox("adapt", true);
    private float yaw;

    public StrafeFix() {
        super("strafefix", Category.MOVEMENT);
        addArguments(adapt);
    }

    @EventHandler(-2) // Ensure we have the latest data
    public void onLivingUpdate(EntityTickedEvent e) {
        assert mc.player != null;
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

    @EventHandler(-2) // Ensure we have the latest data
    public void onKeyboardInput(KeyboardInputEvent e) {
        if (!adapt.getValue()) return;
        assert mc.player != null;
        if (e.moveVector.length() <= 1e-4f) return;

        float cameraRadians = mc.player.getYRot() * Mth.DEG_TO_RAD;
        float aimRadians = yaw * Mth.DEG_TO_RAD;


        Vec2 desired = e.moveVector.rotate(cameraRadians);
        int originalForward = MathUtil.impulse(e.keyPresses.forward(), e.keyPresses.backward());
        int originalStrafe = MathUtil.impulse(e.keyPresses.left(), e.keyPresses.right());

        int bestForward = originalForward;
        int bestStrafe = originalStrafe;
        float bestAngle = e.moveVector.rotate(aimRadians).dot(desired);
        float minimalLengthDifference = 0.0F;
        float referenceLength = moveLength(originalForward, originalStrafe);

        for (int forward = -1; forward <= 1; forward++) {
            for (int strafe = -1; strafe <= 1; strafe++) {
                if (forward == 0 && strafe == 0) continue;
                float angle = new Vec2(strafe, forward).normalized().rotate(aimRadians).dot(desired);
                if (angle < bestAngle - 1e-4f) continue;
                float lengthDifference = Mth.abs(moveLength(forward, strafe) - referenceLength);
                if (angle > bestAngle - 1e-4f || lengthDifference < minimalLengthDifference) {
                    bestForward = forward;
                    bestStrafe = strafe;
                    bestAngle = angle;
                    minimalLengthDifference = lengthDifference;
                }
            }
        }

        e.keyPresses = new Input(
                bestForward > 0,
                bestForward < 0,
                bestStrafe > 0,
                bestStrafe < 0,
                e.keyPresses.jump(),
                e.keyPresses.shift(),
                e.keyPresses.sprint()
        );
        e.moveVector = new Vec2(bestStrafe, bestForward).normalized();
    }

    private static float moveLength(int forward, int strafe) {
        return forward != 0 && strafe != 0 ? 0.98f : 1.0f;
    }
}
