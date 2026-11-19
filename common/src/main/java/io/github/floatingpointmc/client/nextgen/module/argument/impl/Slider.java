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

import java.util.function.Supplier;

public class Slider extends Argument<Double> {
    private final double min, max, increment;

    public Slider(@NotNull String identity, double value, double min, double max, double increment) {
        super(identity, value, () -> true);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public Slider(@NotNull String identity, double value, @NotNull Supplier<Boolean> displayable, double min, double max, double increment) {
        super(identity, value, displayable);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    @Override
    public void setValue(@NonNull Double value) {
        if (value.isInfinite() || value.isNaN()) return;
        value = Math.clamp(value, min, max);
        double rest = value % increment / increment;
        value += Math.round(rest) * increment;
        super.setValue(value);
    }
}
