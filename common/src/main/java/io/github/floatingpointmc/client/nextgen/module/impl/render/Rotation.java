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

package io.github.floatingpointmc.client.nextgen.module.impl.render;

import io.github.floatingpointmc.client.nextgen.event.impl.player.PreMotionEvent;
import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.util.math.RotationUtil;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import org.jetbrains.annotations.NotNull;

public class Rotation extends Module {
    public Rotation() {
        super("rotation", Category.RENDER);
    }

    @EventHandler(-2)
    public void onPreMotion(@NotNull PreMotionEvent e) {
        if (!RotationUtil.isActive()) return;
        assert mc.player != null;
        mc.player.setYHeadRot(e.yaw);
        mc.player.setYBodyRot(e.yaw);
    }
}
