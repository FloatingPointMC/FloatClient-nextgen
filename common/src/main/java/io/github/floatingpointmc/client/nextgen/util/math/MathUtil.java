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

package io.github.floatingpointmc.client.nextgen.util.math;

import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class MathUtil {
    public static double random(double min, double max) {
        if (max < min) return random(max, min);
        return min + (Math.random() * (max - min));
    }

    public static Double interpolate(final double oldValue, final double newValue, final double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }

    public static int reAlpha(int color, int alpha) {
        return (0x00FFFFFF & color) | ((alpha & 0xFF) << 24);
    }

    public static int reBlue(int color, int blue) {
        return (0xFFFFFF00 & color) | ((blue & 0xFF));
    }

    public static int rainbow(int delay, float percent) {
        double rainbow = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / percent);
        return Color.getHSBColor((float) (rainbow % 360.0 / 360.0), 0.5f, 1.0f).getRGB();
    }

    public static boolean isHovered(double x, double y, double x1, double y1, double mouseX, double mouseY) {
        return (x <= mouseX && mouseX <= x1 && y <= mouseY && mouseY <= y1);
    }
}
