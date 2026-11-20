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

package io.github.floatingpointmc.client.nextgen.ui.blur;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;

public final class BlurEffect implements AutoCloseable {
    private static final FullscreenPass FILTER = new FullscreenPass("box_blur", ColorTargetState.DEFAULT, true, false);
    private final EffectTarget swap = new EffectTarget("Float box blur");

    public void render(RenderTarget main, float radius) {
        // At 2^24 a float can no longer advance by one texel throughout the kernel.
        if (!(radius >= 0 && radius < (1 << 24))) {
            throw new IllegalArgumentException("Blur radius must be nonnegative and support unit-spaced taps");
        }
        if (radius == 0) return;
        var temp = swap.size(main.width, main.height);
        FILTER.draw(main, temp, null, false, false, 0, 1, radius, 0);
        FILTER.draw(temp, main, null, false, false, 1, 0, radius, 0);
    }

    @Override
    public void close() {
        swap.close();
    }
}
