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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.pipeline.*;

import java.util.ArrayList;
import java.util.List;

public final class KawaseEffect implements AutoCloseable {
    private static final ColorTargetState ADD = new ColorTargetState(new BlendFunction(BlendFactor.ONE, BlendFactor.ONE));
    private static final FullscreenPass BLUR_DOWN = new FullscreenPass("kawase_down_blur", ColorTargetState.DEFAULT, true, false);
    private static final FullscreenPass BLUR_UP = new FullscreenPass("kawase_up_blur", ColorTargetState.DEFAULT, true, true);
    private static final FullscreenPass BLOOM_DOWN = new FullscreenPass("kawase_down_bloom", ADD, true, false);
    private static final FullscreenPass BLOOM_UP = new FullscreenPass("kawase_up_bloom", ADD, true, true);
    private static final FullscreenPass COMPOSITE = new FullscreenPass("effect_copy",
            new ColorTargetState(new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA)), false, false);

    private final EffectTarget capture = new EffectTarget("Float Kawase capture");
    private final List<EffectTarget> pyramid = new ArrayList<>();
    private RenderTarget captured;

    public RenderTarget begin(RenderTarget main) {
        captured = null;
        var target = capture.size(main.width, main.height);
        RenderSystem.getDevice().createCommandEncoder().clearColorTexture(target.getColorTexture(), FullscreenPass.LEGACY_CLEAR);
        captured = target;
        return target;
    }

    public void render(RenderTarget main, int iterations, int offset, boolean bloom) {
        if (captured == null || captured.width != main.width || captured.height != main.height) {
            throw new IllegalStateException("Begin a Kawase capture at the current framebuffer size first");
        }
        if (iterations < 1) throw new IllegalArgumentException("Kawase requires at least one iteration");
        int maxLevel = Math.max(1, 31 - Integer.numberOfLeadingZeros(Math.max(main.width, main.height)));
        int levels = Math.min(iterations, maxLevel);
        while (pyramid.size() <= levels) pyramid.add(new EffectTarget("Float Kawase level " + pyramid.size()));
        var down = bloom ? BLOOM_DOWN : BLUR_DOWN;
        var up = bloom ? BLOOM_UP : BLUR_UP;
        RenderTarget input = bloom ? captured : main;
        for (int level = 1; level <= levels; level++) {
            var output = level(main, level);
            down.draw(input, output, null, level > 1, bloom,
                    distance(offset, level - 1, bloom), 1f / output.width, 1f / output.height, level > 1 ? 1 : 0);
            input = output;
        }
        for (int level = levels - 1; level >= 0; level--) {
            var output = level(main, level);
            up.draw(input, output, captured, true, bloom, distance(offset, level, bloom),
                    1f / output.width, 1f / output.height, level == 0 ? 1 : 0);
            input = output;
        }
        COMPOSITE.draw(input, main, null, false, false, 0, 0, 0, 0);
    }

    private RenderTarget level(RenderTarget main, int level) {
        return pyramid.get(level).size(Math.max(1, main.width >> level), Math.max(1, main.height >> level));
    }

    private static float distance(int offset, int exponent, boolean bloom) {
        return bloom ? offset / (float) Math.pow(1.5, exponent) : offset;
    }

    @Override
    public void close() {
        captured = null;
        capture.close();
        pyramid.forEach(EffectTarget::close);
        pyramid.clear();
        FullscreenPass.releaseGeometry();
    }
}
