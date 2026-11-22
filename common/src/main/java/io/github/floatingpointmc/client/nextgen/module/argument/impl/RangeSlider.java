package io.github.floatingpointmc.client.nextgen.module.argument.impl;

import io.github.floatingpointmc.client.nextgen.module.argument.Argument;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class RangeSlider extends Argument<Double> {
    @Getter
    public final double minimum, maximum, increment;
    @Getter
    private double minValue;
    @Getter
    private double maxValue;

    public RangeSlider(@NotNull String identity, double minValue, double maxValue, double minimum, double maximum, double increment) {
        super(identity, .0, () -> true);
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public RangeSlider(@NotNull String identity, double minValue, double maxValue, @NotNull Supplier<Boolean> displayable, double minimum, double maximum, double increment) {
        super(identity, .0, displayable);
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public @NonNull Double getValue() {
        return MathUtil.random(minValue, maxValue);
    }

    public void setMinValue(double minValue) {
        minValue = Math.clamp(minValue, minimum, maxValue);
        double rest = minValue % increment;
        minValue -= rest;
        rest /= increment;
        minValue += Math.round(rest) * increment;
        this.minValue = minValue;
    }

    public void setMaxValue(double maxValue) {
        maxValue = Math.clamp(maxValue, minimum, maxValue);
        double rest = maxValue % increment;
        maxValue -= rest;
        rest /= increment;
        maxValue += Math.round(rest) * increment;
        this.maxValue = maxValue;
    }

    @Override
    public void setValue(@NonNull Double value) {
        throw new UnsupportedOperationException("Use setMinValue and setMaxValue instead.");
    }
}
