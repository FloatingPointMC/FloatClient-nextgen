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

package io.github.floatingpointmc.client.nextgen.module;

import io.github.floatingpointmc.client.nextgen.module.argument.Argument;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Module {
    private final String identity;
    private final Category category;
    private final List<Argument<?>> arguments;
    private final boolean toggleable;

    public Module(String identity, Category category) {
        this(identity, category, List.of(), true);
    }

    public Module(String identity, Category category, boolean toggleable) {
        this(identity, category, List.of(), toggleable);
    }

    public Module(String identity, Category category, List<Argument<?>> arguments) {
        this(identity, category, arguments, true);
    }
}
