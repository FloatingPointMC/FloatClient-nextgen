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

package io.github.floatingpointmc.client.nextgen.ui.shader.state;

import com.mojang.blaze3d.pipeline.RenderTarget;
import io.github.floatingpointmc.client.nextgen.mixin.interfaces.IGuiGraphicsExtractor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiRenderState;

import java.util.function.Consumer;

/** A snapshot taken during GUI extraction. Submission adds work to vanilla's ordered GUI layers. */
public abstract class OperationRenderState {
    public final void submit(GuiGraphicsExtractor graphics) {
        submit(((IGuiGraphicsExtractor) graphics).getGuiRenderState());
    }

    public abstract void submit(GuiRenderState state);

    @FunctionalInterface
    public interface Operation {
        void render(RenderTarget destination, Consumer<RenderTarget> drawContents);
    }
}
