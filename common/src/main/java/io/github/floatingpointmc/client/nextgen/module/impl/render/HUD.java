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

import io.github.floatingpointmc.client.nextgen.FloatClientNextgen;
import io.github.floatingpointmc.client.nextgen.event.impl.render.Render2DEvent;
import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.ModuleRegistry;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.CheckBox;
import io.github.floatingpointmc.client.nextgen.ui.clickui.Colors;
import io.github.floatingpointmc.client.nextgen.ui.font.FontRegistry;
import io.github.floatingpointmc.client.nextgen.util.render.BlurUtil;
import io.github.floatingpointmc.client.nextgen.util.render.RenderUtil;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class HUD extends Module {
    private static final Identifier ICON = Identifier.fromNamespaceAndPath("floatclient_nextgen", "icons/icon_256x256.png");
    private static final CheckBox bloom = new CheckBox("bloom", true);

    public HUD() {
        super("hud", Category.RENDER);
        ModuleRegistry.enable(this);
    }

    @EventHandler
    public void onRender2D(@NotNull Render2DEvent e) {
        String client = String.format("%s %s%s", FloatClientNextgen.NAME, FloatClientNextgen.EDITION, FloatClientNextgen.VERSION);
        float clientWidth = FontRegistry.SANS.width(client, 8);
        String fps = "FPS: " + mc.getFps();
        float fpsWidth = FontRegistry.SANS.width(fps, 8) + 30;
        if (bloom.getValue()) {
            BlurUtil.bloomKawase(e.graphics, 3, 2,
                    RenderUtil.roundedRect(e.graphics, 6, 7, clientWidth + fpsWidth + 10, 27, 3, Colors.MAIN_THEME)
            );
        }
        RenderUtil.drawRoundedRect(e.graphics, 6, 7, fpsWidth + clientWidth + 10, 27, 3, new Color(15, 15, 20, 121).getRGB());
        RenderUtil.drawImage(e.graphics, ICON, 9, 9, 16, 16);
        FontRegistry.SANS.drawString(e.graphics, client, 8, 26, 5, 0xFFFCFCFC, true);
        FontRegistry.SANS.drawString(e.graphics, "|", 8, 26 + clientWidth + 2, 4, 0xFFFCFCFC, true);
        FontRegistry.SANS.drawString(e.graphics, fps, 8, 26 + clientWidth + 10, 5, 0xFFFCFCFC, true);
    }
}
