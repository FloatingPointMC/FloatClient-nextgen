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

package io.github.floatingpointmc.client.nextgen.command;

import io.github.floatingpointmc.client.nextgen.command.impl.Bind;
import io.github.floatingpointmc.client.nextgen.command.impl.Toggle;
import io.github.floatingpointmc.client.nextgen.event.impl.player.ChatEvent;
import io.github.floatingpointmc.client.nextgen.util.player.PlayerUtil;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import io.github.vlouboos.standaloneevent.api.StandaloneEventAPI;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashBigSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import lombok.extern.log4j.Log4j2;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Log4j2
public final class CommandRegistry {
    private static final @NotNull ReferenceSet<Command> commands;

    static {
        ReferenceSet<Command> cmdSet = new ReferenceOpenHashBigSet<>();
        cmdSet.add(new Bind());
        cmdSet.add(new Toggle());
        commands = ReferenceSets.unmodifiable(cmdSet);
        StandaloneEventAPI.getApi().register(CommandRegistry.class);
    }

    public static void init() {
        log.info("Loaded {} commands.", commands.size());
    }

    @EventHandler
    public static void onChat(@NotNull ChatEvent e) {
        if (e.message.length() > 1 && e.message.startsWith(".")) {
            e.canceled = true;
            String[] cmd = e.message.substring(1).split(" ");
            String command = cmd[0];
            String[] args = Arrays.copyOfRange(cmd, 1, cmd.length);
            for (Command impl : commands) {
                if (impl.aliases.contains(command.toLowerCase())) {
                    impl.execute(args);
                    return;
                }
            }
            PlayerUtil.addMessage(Component.literal("No command named '%s' found!".formatted(command)).withColor(TextColor.RED));
        }
    }
}
