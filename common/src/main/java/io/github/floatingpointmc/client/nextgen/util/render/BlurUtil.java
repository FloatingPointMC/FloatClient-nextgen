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

package io.github.floatingpointmc.client.nextgen.util.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import io.github.floatingpointmc.client.nextgen.ui.blur.BlurEffect;
import io.github.floatingpointmc.client.nextgen.ui.blur.KawaseEffect;
import net.minecraft.client.Minecraft;

/** Render-thread operations, called between passes after submitting the source image. */
public final class BlurUtil {
    private static final BlurEffect BLUR = new BlurEffect();
    private static final KawaseEffect KAWASE = new KawaseEffect();
    private static boolean pendingBloom;
    private static int pendingIterations, pendingOffset;
    private static RenderTarget captureTarget;
    private static boolean captureActive;

    private BlurUtil() {}

    private static RenderTarget getTarget() {
        return Minecraft.getInstance().gameRenderer.mainRenderTarget();
    }

    public static void blurAll(float radius) {
        BLUR.render(getTarget(), radius);
    }

    /** Borrow this cleared destination for subsequent render passes. Finish those passes before filtering. */
    public static RenderTarget beginKawaseCapture() {
        if (captureTarget == null) captureTarget = KAWASE.begin(getTarget());
        captureActive = true;
        return captureTarget;
    }

    /** Target currently selected during GUI extraction, or null for normal GUI output. */
    public static RenderTarget currentCaptureTarget() {
        return captureActive ? captureTarget : null;
    }

    public static void blurKawase(int iterations, int offset) {
        KAWASE.render(getTarget(), iterations, offset, false);
    }

    public static void bloomKawase(int iterations, int offset) {
        pendingIterations = iterations;
        pendingOffset = offset;
        pendingBloom = true;
        captureActive = false;
    }

    public static void flushPendingBloom() {
        if (!pendingBloom) return;
        pendingBloom = false;
        KAWASE.render(getTarget(), pendingIterations, pendingOffset, true);
        captureTarget = null;
    }

    /** Ends extraction capture even when the caller did not request a filter pass. */
    public static void endCaptureFrame() {
        captureActive = false;
        if (!pendingBloom) captureTarget = null;
    }

    public static void release() {
        pendingBloom = false;
        captureTarget = null;
        captureActive = false;
        BLUR.close();
        KAWASE.close();
    }
}
