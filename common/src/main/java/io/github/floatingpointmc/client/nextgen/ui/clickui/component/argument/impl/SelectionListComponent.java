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

package io.github.floatingpointmc.client.nextgen.ui.clickui.component.argument.impl;

import io.github.floatingpointmc.client.nextgen.language.LanguageManager;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.SelectionList;
import io.github.floatingpointmc.client.nextgen.ui.clickui.ClickUI;
import io.github.floatingpointmc.client.nextgen.ui.clickui.Colors;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.TopLayer;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.argument.ArgumentComponent;
import io.github.floatingpointmc.client.nextgen.ui.font.FontRegistry;
import io.github.floatingpointmc.client.nextgen.util.math.FunctionUtil;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import io.github.floatingpointmc.client.nextgen.util.render.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class SelectionListComponent extends ArgumentComponent<SelectionList> {
    private final TopLayer layer = new TopLayer() {
        private static final Identifier SELECTED = Identifier.fromNamespaceAndPath("floatclient_nextgen", "clickui/selected.png");
        private FunctionUtil<Integer> alpha;
        private FunctionUtil<Float> pos;
        private boolean expand = true;

        @Override
        public void init(int x, int y) {
            super.init(x, y);
            expand = true;
            alpha = FunctionUtil.cubicBezier(0, 255, 160, 0.25f, 1, 0.5f, 1);
            pos = FunctionUtil.cubicBezier(-6.0f, 0, 160, 0.25f, 1, 0.5f, 1);
        }

        @Override
        public void draw(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
            int alpha = Math.min(this.alpha.calculate(), ClickUI.alphaAnimation.calculate());
            float offset = pos.calculate();
            int bgColor = MathUtil.reAlpha(0x16121C, (int) (alpha * 0.92f));
            int borderColor = MathUtil.reAlpha(Colors.MAIN_THEME, (int) (alpha * 0.48f));
            RenderUtil.drawRoundedRect(graphics, x, y + offset, x + 72, y + 18 * argument.getModes().size() + 6 + offset, 2, bgColor);
            RenderUtil.drawRoundedOutline(graphics, x, y + offset, x + 72, y + 18 * argument.getModes().size() + 6 + offset, 2, 1, borderColor);
            for (int i = 0; i < argument.getModes().size(); i++) {
                String mode = argument.getModes().get(i);
                if (argument.isCurrent(mode)) {
                    RenderUtil.drawRoundedRect(graphics, x + 3, y + 18 * i + 3 + offset, x + 69, y + 18 * i + 21 + offset, 2, MathUtil.reAlpha(Colors.MAIN_THEME, (int) (alpha * 0.28f)));
                    FontRegistry.GEIST.drawString(graphics, getValue(mode), 3.5f, x + 10, y + 18 * i + 7 + offset, MathUtil.reAlpha(Colors.EXTREME_LIGHT_THEME, alpha));
                    RenderUtil.drawImage(graphics, SELECTED, x + 56, (int) (y + 18 * i + 8 + offset), 8, 8, MathUtil.reAlpha(0xFFFFFFFF, alpha));
                } else {
                    FontRegistry.GEIST.drawString(graphics, getValue(mode), 3.5f, x + 10, y + 18 * i + 7 + offset, MathUtil.reAlpha(0x9696A0, alpha));
                }
            }
        }

        @Override
        public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (MathUtil.isHovered(x, y - 20, x + 72, y - 2, mouseX, mouseY)) return false;
            if (expand) {
                for (int i = 0; i < argument.getModes().size(); i++) {
                    if (MathUtil.isHovered(x + 3, y + 18 * i + 3, x + 69, y + 18 * i + 21, mouseX, mouseY)) {
                        argument.setValue(argument.getModes().get(i));
                        break;
                    }
                }
                finish();
                return true;
            }
            return false;
        }

        @Override
        public void finish() {
            expand = false;
            alpha = FunctionUtil.cubicBezier(255, 0, 160, 0.25f, 1, 0.5f, 1);
            pos = FunctionUtil.cubicBezier(0, -6.0f, 160, 0.25f, 1, 0.5f, 1);
            expanded = false;
            angle = FunctionUtil.linear(angle.calculate(), -90, 200);
            borderRed = FunctionUtil.linear(borderRed.calculate(), 255, 200);
            borderGreen = FunctionUtil.linear(borderGreen.calculate(), 255, 200);
            borderBlue = FunctionUtil.linear(borderBlue.calculate(), 255, 200);
            borderAlpha = FunctionUtil.linear(borderAlpha.calculate(), 15, 200);
            bgRed = FunctionUtil.linear(bgRed.calculate(), 0, 200);
            bgGreen = FunctionUtil.linear(bgGreen.calculate(), 0, 200);
            bgBlue = FunctionUtil.linear(bgBlue.calculate(), 0, 200);
            bgAlpha = FunctionUtil.linear(bgAlpha.calculate(), 89, 200);
        }

        @Override
        public boolean finished() {
            return alpha.calculate() == 0 && !expand;
        }
    };
    private @NotNull FunctionUtil<Integer> angle = FunctionUtil.done(-90), borderRed = FunctionUtil.done(255), borderGreen = FunctionUtil.done(255), borderBlue = FunctionUtil.done(255), borderAlpha = FunctionUtil.done(15), bgRed = FunctionUtil.done(0), bgGreen = FunctionUtil.done(0), bgBlue = FunctionUtil.done(0), bgAlpha = FunctionUtil.done(89);
    public boolean expanded = false, currentExpanded;

    public SelectionListComponent(@NotNull Module parent, @NonNull SelectionList argument) {
        super(parent, argument);
    }

    @Override
    public void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        int pureWhite = MathUtil.reAlpha(0xD8D8D8, ClickUI.alphaAnimation.calculate());
        int pureGrey = MathUtil.reAlpha(0xC3C3C3, ClickUI.alphaAnimation.calculate());
        if (currentExpanded != expanded) {
            currentExpanded = expanded;
            angle = FunctionUtil.linear(angle.calculate(), expanded ? 90 : -90, 200);
            borderRed = FunctionUtil.linear(borderRed.calculate(), expanded ? 112 : 255, 200);
            borderGreen = FunctionUtil.linear(borderGreen.calculate(), expanded ? 74 : 255, 200);
            borderBlue = FunctionUtil.linear(borderBlue.calculate(), expanded ? 189 : 255, 200);
            borderAlpha = FunctionUtil.linear(borderAlpha.calculate(), expanded ? 153 : 15, 200);
            bgRed = FunctionUtil.linear(bgRed.calculate(), expanded ? 112 : 0, 200);
            bgGreen = FunctionUtil.linear(bgGreen.calculate(), expanded ? 74 : 0, 200);
            bgBlue = FunctionUtil.linear(bgBlue.calculate(), expanded ? 189 : 0, 200);
            bgAlpha = FunctionUtil.linear(bgAlpha.calculate(), expanded ? 56 : 89, 200);
            if (expanded) {
                layer.init(x, y + 32);
                ClickUI.topLayer = layer;
            } else {
                layer.finish();
            }
        }
        FontRegistry.GEIST.drawString(graphics, getArgumentName(), 3.5f, x, y, pureWhite);
        RenderUtil.drawRoundedRect(graphics, x, y + 12, x + 72, y + 30, 2, bgRed.calculate() << 16 | bgGreen.calculate() << 8 | bgBlue.calculate() | bgAlpha.calculate() << 24);
        RenderUtil.drawRoundedOutline(graphics, x, y + 12, x + 72, y + 30, 2, 2f, borderRed.calculate() << 16 | borderGreen.calculate() << 8 | borderBlue.calculate() | borderAlpha.calculate() << 24);
        FontRegistry.GEIST.drawString(graphics, getValue(argument.getValue()), 3.5f, x + 6.5f, y + 16.5f, MathUtil.reAlpha(Colors.MAIN_THEME, (int) (ClickUI.alphaAnimation.calculate() * 0.55f)));
        FontRegistry.GEIST.drawString(graphics, getValue(argument.getValue()), 3.5f, x + 6, y + 16, MathUtil.reAlpha(Colors.EXTREME_LIGHT_THEME, ClickUI.alphaAnimation.calculate()));
        RenderUtil.drawChevron(graphics, x + 64, y + 21, angle.calculate(), pureGrey);
    }

    @Override
    public void onMouseClicked(int x, int y, double mouseX, double mouseY) {
        if (MathUtil.isHovered(x, y + 12, x + 72, y + 30, mouseX, mouseY)) {
            expanded = !expanded;
        }
    }

    @Override
    public int getHeight() {
        return 34;
    }

    private String getValue(@NotNull String value) {
        return LanguageManager.get("module." + parent.getIdentity() + "." + argument.identity + "." + value);
    }
}
