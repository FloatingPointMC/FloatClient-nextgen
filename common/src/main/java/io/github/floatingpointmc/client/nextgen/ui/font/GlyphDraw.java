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

package io.github.floatingpointmc.client.nextgen.ui.font;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.UniformType;
import io.github.floatingpointmc.client.nextgen.ui.shader.Shader;
import io.github.floatingpointmc.client.nextgen.ui.shader.state.impl.ShaderRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.joml.Matrix3x2f;

import java.util.ArrayList;
import java.util.List;

public final class GlyphDraw {
    private static final RenderPipeline PIPELINE = Shader.positionUvPipeline("glyph", Shader.transforms()
            .withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER)
            .withUniform("Parameters", UniformType.UNIFORM_BUFFER).build(), new ColorTargetState(BlendFunction.TRANSLUCENT));

    private record Quad(GlyphAtlas.Tile tile, float x, float y) {
        void emit(VertexConsumer out, Matrix3x2f pose) {
            float right = x + tile.w() * tile.scale(), bottom = y + tile.h() * tile.scale();
            float u = (float)tile.u()/GlyphAtlas.SIDE, v = (float)tile.v()/GlyphAtlas.SIDE;
            float u2 = (float)(tile.u()+tile.w())/GlyphAtlas.SIDE, v2 = (float)(tile.v()+tile.h())/GlyphAtlas.SIDE;
            out.addVertexWith2DPose(pose, x, y).setUv(u, v);
            out.addVertexWith2DPose(pose, x, bottom).setUv(u, v2);
            out.addVertexWith2DPose(pose, right, bottom).setUv(u2, v2);
            out.addVertexWith2DPose(pose, x, y).setUv(u, v);
            out.addVertexWith2DPose(pose, right, bottom).setUv(u2, v2);
            out.addVertexWith2DPose(pose, right, y).setUv(u2, v);
        }
    }

    public static void submit(GuiRenderState state, GuiTextLayout layout, Matrix3x2f pose, ScreenRectangle clip,
                              int argb, boolean shadow) {
        float zoom = (float)Math.max(Math.hypot(pose.m00(), pose.m01()), Math.hypot(pose.m10(), pose.m11()));
        float density = Math.max(1, Math.round(Minecraft.getInstance().getWindow().getGuiScale() * zoom));
        var quads = new ArrayList<Quad>();
        GlyphAtlas.Page page = null;
        int color = 0;
        for (var glyph : layout.glyphs) {
            var tile = GlyphAtlas.glyph(glyph.face(), glyph.id(), density);
            if (tile == null) continue;
            int tint = glyph.rgb() == null ? argb : (argb & 0xff000000) | glyph.rgb();
            if (shadow) tint = (tint & 0xff000000) | ((tint & 0xfcfcfc) >>> 2);
            if (page != tile.page() || tint != color) {
                if (!quads.isEmpty()) {
                    submitRun(state, pose, clip, color, page, quads);
                    quads = new ArrayList<>();
                }
                page = tile.page();
                color = tint;
            }
            float offset = shadow ? 1 : 0;
            quads.add(new Quad(tile, glyph.x() + tile.left() + offset, glyph.baseline() + tile.top() + offset));
        }
        if (!quads.isEmpty()) submitRun(state, pose, clip, color, page, quads);
    }

    private static void submitRun(GuiRenderState state, Matrix3x2f pose, ScreenRectangle clip, int argb,
                                  GlyphAtlas.Page page, List<Quad> quads) {
        float left = Float.POSITIVE_INFINITY, top = left, right = -left, bottom = right;
        for (var q : quads) {
            left = Math.min(left, q.x);
            top = Math.min(top, q.y);
            right = Math.max(right, q.x + q.tile.w()*q.tile.scale());
            bottom = Math.max(bottom, q.y + q.tile.h()*q.tile.scale());
        }
        int x = (int)Math.floor(left), y = (int)Math.floor(top);
        var bounds = new ScreenRectangle(x, y, (int)Math.ceil(right)-x, (int)Math.ceil(bottom)-y).transformMaxBounds(pose);
        if (clip != null) bounds = bounds.intersection(clip);
        if (bounds == null) return;
        new ShaderRenderState(PIPELINE, page.setup, clip, bounds, out -> quads.forEach(q -> q.emit(out, pose)),
                new ShaderRenderState.Uniforms(new ShaderRenderState.UniformI(argb))).submit(state);
    }
}
