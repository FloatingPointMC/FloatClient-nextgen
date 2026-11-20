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

package io.github.floatingpointmc.client.nextgen.module.argument.impl;

import io.github.floatingpointmc.client.nextgen.module.argument.Argument;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Supplier;

public class SelectionList extends Argument<String> {
    private final List<String> modes;

    public SelectionList(@NotNull String identity, @NotNull String value, @NotNull String @NotNull ... selectables) {
        super(identity, value, () -> true);
        modes = List.of(selectables);
    }

    public SelectionList(@NotNull String identity, @NotNull String value, @NotNull Supplier<Boolean> displayable, @NotNull String @NotNull ... selectables) {
        super(identity, value, displayable);
        modes = List.of(selectables);
    }

    @Override
    public void setValue(@NonNull String value) {
        if (modes.contains(value)) super.setValue(value);
    }

    public boolean isCurrent(@NotNull String selection) {
        return getValue().equals(selection);
    }
}
