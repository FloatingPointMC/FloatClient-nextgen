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

import io.github.floatingpointmc.client.nextgen.mixin.interfaces.IGuiGraphicsExtractor;
import io.github.floatingpointmc.client.nextgen.ui.blur.BlurEffect;
import io.github.floatingpointmc.client.nextgen.ui.blur.FullscreenPass;
import io.github.floatingpointmc.client.nextgen.ui.blur.KawaseEffect;
import io.github.floatingpointmc.client.nextgen.ui.shader.state.OperationRenderState;
import io.github.floatingpointmc.client.nextgen.ui.shader.state.OperationBoundary;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public final class BlurUtil {
    private static final BlurEffect BOX = new BlurEffect();
    private static final KawaseEffect KAWASE = new KawaseEffect();

    private BlurUtil() {}

    public static void blurAll(GuiGraphicsExtractor graphics, float radius) {
        if (!(radius >= 0 && radius < (1 << 24))) throw new IllegalArgumentException("Invalid blur radius");
        if (radius == 0) return;
        publish(graphics, (main, ignored) -> BOX.render(main, radius));
    }

    public static void bloomKawase(GuiGraphicsExtractor graphics, int iterations, int offset, OperationRenderState... mask) {
        kawase(graphics, iterations, offset, true, mask);
    }

    public static void blurKawase(GuiGraphicsExtractor graphics, int iterations, int offset, OperationRenderState... mask) {
        kawase(graphics, iterations, offset, false, mask);
    }

    private static void kawase(GuiGraphicsExtractor graphics, int iterations, int offset, boolean bloom, OperationRenderState[] mask) {
        if (iterations < 1) throw new IllegalArgumentException("Expected at least one Kawase level");
        if (mask.length == 0) return;
        publish(graphics, (main, draw) -> {
            var target = KAWASE.begin(main);
            draw.accept(target);
            KAWASE.render(main, target, iterations, offset, bloom);
        }, mask);
    }

    private static void publish(GuiGraphicsExtractor graphics, OperationRenderState.Operation operation,
                                OperationRenderState... elements) {
        var state = ((IGuiGraphicsExtractor) graphics).getGuiRenderState();
        var bounds = new ScreenRectangle(0, 0, graphics.guiWidth(), graphics.guiHeight());
        state.nextStratum();
        state.addGuiElement(new OperationBoundary(bounds, operation));
        for (var element : elements) {
            if (element == null) continue;
            state.nextStratum();
            element.submit(state);
        }
        state.nextStratum();
        state.addGuiElement(new OperationBoundary(bounds, null));
        state.nextStratum();
    }

    public static void release() {
        BOX.close();
        KAWASE.close();
        FullscreenPass.releaseGeometry();
    }
}
