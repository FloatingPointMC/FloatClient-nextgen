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

package io.github.floatingpointmc.client.nextgen.command.impl;

import io.github.floatingpointmc.client.nextgen.command.Command;
import io.github.floatingpointmc.client.nextgen.module.ModuleRegistry;
import io.github.floatingpointmc.client.nextgen.util.player.PlayerUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class Toggle extends Command {
    public Toggle() {
        super(List.of("toggle", "t"));
    }

    @Override
    public void execute(@NotNull String @NotNull [] args) {
        if (args.length != 1) {
            PlayerUtil.addMessage(Component.literal("Usage: .toggle <module>").withColor(TextColor.RED));
            return;
        }
        var module = ModuleRegistry.get(args[0]);
        if (module.isEmpty()) {
            PlayerUtil.addMessage(Component.literal("Module '%s' not found.".formatted(args[0])).withColor(TextColor.RED));
            return;
        }
        module.get().toggle();
        PlayerUtil.addMessage(Component.literal("Toggled module ").withColor(TextColor.GREEN)
                .append(Component.literal(module.get().getName())).withColor(TextColor.YELLOW)
                .append(Component.literal("!").withColor(TextColor.GREEN))
        );
    }
}
