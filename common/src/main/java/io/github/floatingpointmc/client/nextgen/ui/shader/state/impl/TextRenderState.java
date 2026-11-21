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

package io.github.floatingpointmc.client.nextgen.ui.shader.state.impl;

import io.github.floatingpointmc.client.nextgen.mixin.interfaces.IGuiGraphicsExtractor;
import io.github.floatingpointmc.client.nextgen.ui.shader.state.OperationRenderState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;

public final class TextRenderState extends OperationRenderState {
    private final GuiTextRenderState text;
    private final boolean visible;

    public TextRenderState(GuiGraphicsExtractor graphics, Font font, Component component,
                           int x, int y, int color, float scale, boolean shadow) {
        var pose = new Matrix3x2f(graphics.pose());
        // Scale about the text origin. Keep vanilla glyph positions for the ordinary scale.
        if (scale != 1f) pose.translate(x, y).scale(scale).translate(-x, -y);
        text = new GuiTextRenderState(font, component.getVisualOrderText(), pose, x, y, color, 0,
                shadow, false, ((IGuiGraphicsExtractor) graphics).floatclient$getScissor());
        visible = (color >>> 24) != 0;
    }

    @Override
    public void submit(GuiRenderState state) {
        if (visible) state.addText(text);
    }
}
