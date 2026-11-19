package io.github.floatingpointmc.client.nextgen.module.argument.impl;

import io.github.floatingpointmc.client.nextgen.module.argument.Argument;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class RangeSlider extends Argument<Double> {
    public final double min, max, increment;
    @Getter
    private double minValue;
    @Getter
    private double maxValue;

    public RangeSlider(@NotNull String identity, double minValue, double maxValue, double min, double max, double increment) {
        super(identity, .0, () -> true);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public RangeSlider(@NotNull String identity, double minValue, double maxValue, @NotNull Supplier<Boolean> displayable, double min, double max, double increment) {
        super(identity, .0, displayable);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public @NonNull Double getValue() {
        return MathUtil.random(minValue, maxValue);
    }

    public void setMinValue(double minValue) {
        minValue = Math.clamp(minValue, min, maxValue);
        double rest = minValue % increment / increment;
        minValue += Math.round(rest) * increment;
        this.minValue = minValue;
    }

    public void setMaxValue(double maxValue) {
        maxValue = Math.clamp(maxValue, min, maxValue);
        double rest = maxValue % increment / increment;
        maxValue += Math.round(rest) * increment;
        this.maxValue = maxValue;
    }

    @Override
    public void setValue(@NonNull Double value) {
        throw new UnsupportedOperationException("Use setMinValue and setMaxValue instead.");
    }
}
