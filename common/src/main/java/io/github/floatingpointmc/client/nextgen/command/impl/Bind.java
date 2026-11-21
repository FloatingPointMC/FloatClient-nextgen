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
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.ModuleRegistry;
import io.github.floatingpointmc.client.nextgen.util.io.KeyboardUtil;
import io.github.floatingpointmc.client.nextgen.util.player.PlayerUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public final class Bind extends Command {
    public Bind() {
        super(List.of("bind", "b", "binding", "bound"));
    }

    @Override
    public void execute(@NotNull String @NotNull [] args) {
        if (args.length != 2) {
            PlayerUtil.addMessage(Component.literal("Usage: .bind <module> <key>").withColor(TextColor.RED));
            return;
        }
        Optional<Module> module = ModuleRegistry.get(args[0].toLowerCase());
        if (module.isEmpty()) {
            PlayerUtil.addMessage(Component.literal("Module '%s' not found!".formatted(args[0])).withColor(TextColor.RED));
            return;
        }
        int key = KeyboardUtil.NAME_TO_KEY.getOrDefault(args[1].toUpperCase(), -1);
        if (key == -1) {
            PlayerUtil.addMessage(Component.literal("Unknown key '%s'!".formatted(args[1])).withColor(TextColor.RED));
            return;
        }
        module.get().setKey(key);
        PlayerUtil.addMessage(Component.literal("Bound ").withColor(TextColor.GREEN)
                .append(Component.literal(module.get().getName()).withColor(TextColor.AQUA))
                .append(Component.literal(" to ").withColor(TextColor.GREEN))
                .append(Component.literal(KeyboardUtil.KEY_TO_NAME.get(key)).withColor(TextColor.YELLOW))
                .append(Component.literal("!").withColor(TextColor.GREEN))
        );
    }
}
