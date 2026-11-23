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

import io.github.floatingpointmc.client.nextgen.event.impl.render.Render3DEvent;
import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.SelectionList;
import io.github.floatingpointmc.client.nextgen.module.impl.misc.Target;
import io.github.floatingpointmc.client.nextgen.util.render.RenderUtil;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ESP extends Module {
    private final SelectionList mode = new SelectionList("mode", "box", "box", "2d");

    public ESP() {
        super("esp", Category.RENDER);
        addArguments(mode);
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        assert mc.level != null;
        switch (mode.getValue()) {
            case "box"-> {
                for (Entity entity : mc.level.entitiesForRendering()) {
                    if (Target.notQualified(entity)) continue;
                    AABB box = entity.getBoundingBox();
                    float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
                    Vec3 interpolatedPosition = entity.getPosition(partialTick);
                    Vec3 currentPosition = entity.position();
                    double centerX = interpolatedPosition.x - currentPosition.x;
                    double centerY = interpolatedPosition.y - currentPosition.y;
                    double centerZ = interpolatedPosition.z - currentPosition.z;
                    RenderUtil.drawBoundingBox(box.move(centerX, centerY, centerZ), 0xFFFFFFFF, 0x3DFFFFFF);
                }
            }
            case "2d"-> {
                for (Entity entity : mc.level.entitiesForRendering()) {
                    if (Target.notQualified(entity)) continue;
                    RenderUtil.draw2DESPOnEntity(entity, 0xFFFFFFFF, 0);
                }
            }
        }
    }
}
