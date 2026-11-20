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

import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public abstract class Command {
    protected final Minecraft mc = Minecraft.getInstance();
    public final List<String> aliases;

    public abstract void execute(@NotNull String @NotNull [] args);
}
