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
import io.github.floatingpointmc.client.nextgen.ui.font.GlyphDraw;
import io.github.floatingpointmc.client.nextgen.ui.font.GuiTextLayout;
import io.github.floatingpointmc.client.nextgen.ui.shader.state.OperationRenderState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.joml.Matrix3x2f;

public final class TextRenderState extends OperationRenderState {
    private final GuiTextLayout layout;
    private final Matrix3x2f pose;
    private final ScreenRectangle clip;
    private final int color;
    private final boolean shadow;

    public TextRenderState(GuiGraphicsExtractor graphics, GuiTextLayout layout,
                           float x, float y, int color, boolean shadow) {
        this.layout = layout;
        pose = new Matrix3x2f(graphics.pose()).translate(x, y);
        clip = ((IGuiGraphicsExtractor) graphics).floatclient$getScissor();
        this.color = color;
        this.shadow = shadow;
    }

    @Override
    public void submit(GuiRenderState state) {
        if ((color >>> 24) == 0) return;
        if (shadow) GlyphDraw.submit(state, layout, pose, clip, color, true);
        GlyphDraw.submit(state, layout, pose, clip, color, false);
    }
}
