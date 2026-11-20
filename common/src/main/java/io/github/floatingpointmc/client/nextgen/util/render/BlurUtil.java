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
import io.github.floatingpointmc.client.nextgen.ui.shader.ShaderRenderState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class BlurUtil {
    private static final BlurEffect BOX = new BlurEffect();
    private static final KawaseEffect KAWASE = new KawaseEffect();

    private BlurUtil() {}

    public static void blurAll(GuiGraphicsExtractor graphics, float radius) {
        if (!(radius >= 0 && radius < (1 << 24))) throw new IllegalArgumentException("Invalid blur radius");
        if (radius == 0) return;
        // The renderer executes this command in place of drawing the mesh.
        var marker = new ShaderRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), null,
                new ScreenRectangle(0, 0, graphics.guiWidth(), graphics.guiHeight()), out -> {});
        publish(graphics, List.of(marker), (main, ignored) -> BOX.render(main, radius));
    }

    /** A CPU-side list of mask primitives. Nothing is drawn until a filter is submitted. */
    public static final class Capture {
        private final GuiGraphicsExtractor graphics;
        private final List<ShaderRenderState> elements = new ArrayList<>();

        private Capture(GuiGraphicsExtractor graphics) {
            this.graphics = graphics;
        }

        public void roundedRect(float x, float y, float x2, float y2, float radius, int argb) {
            var element = RenderUtil.roundedRect(graphics, x, y, x2, y2, radius, argb);
            if (element != null) elements.add(element);
        }
    }

    public static void bloomKawase(GuiGraphicsExtractor graphics, int iterations, int offset, Consumer<Capture> mask) {
        kawase(graphics, iterations, offset, mask, true);
    }

    public static void blurKawase(GuiGraphicsExtractor graphics, int iterations, int offset, Consumer<Capture> mask) {
        kawase(graphics, iterations, offset, mask, false);
    }

    private static void kawase(GuiGraphicsExtractor graphics, int iterations, int offset, Consumer<Capture> mask, boolean bloom) {
        if (iterations < 1) throw new IllegalArgumentException("Expected at least one Kawase level");
        var capture = new Capture(graphics);
        mask.accept(capture);
        if (capture.elements.isEmpty()) return;
        publish(graphics, capture.elements, (main, draw) -> {
            var target = KAWASE.begin(main);
            draw.accept(target);
            KAWASE.render(main, target, iterations, offset, bloom);
        });
    }

    private static void publish(GuiGraphicsExtractor graphics, List<ShaderRenderState> elements, ShaderRenderState.Operation operation) {
        var state = ((IGuiGraphicsExtractor) graphics).getGuiRenderState();
        state.nextStratum();
        for (var element : elements) state.addGuiElement(element.routed(operation));
        state.nextStratum();
    }

    public static void release() {
        BOX.close();
        KAWASE.close();
        FullscreenPass.releaseGeometry();
    }
}
