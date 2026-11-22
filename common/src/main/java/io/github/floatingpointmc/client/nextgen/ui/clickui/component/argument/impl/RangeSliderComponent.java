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

import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.RangeSlider;
import io.github.floatingpointmc.client.nextgen.ui.clickui.ClickUI;
import io.github.floatingpointmc.client.nextgen.ui.clickui.Colors;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.argument.ArgumentComponent;
import io.github.floatingpointmc.client.nextgen.ui.font.FontRegistry;
import io.github.floatingpointmc.client.nextgen.util.math.FunctionUtil;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import io.github.floatingpointmc.client.nextgen.util.render.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.text.DecimalFormat;

public class RangeSliderComponent extends ArgumentComponent<RangeSlider> {
    private static final DecimalFormat formatter = new DecimalFormat("#.####");
    private FunctionUtil<Float> sliderLeft, sliderRight;
    private float expectedPosLeft, expectedPosRight;
    private int dragging = 0;

    public RangeSliderComponent(@NotNull Module parent, @NonNull RangeSlider argument) {
        super(parent, argument);
        float percentage = (float) ((argument.getMinValue() - argument.getMinimum()) / (argument.getMaximum() - argument.getMinimum()));
        if (expectedPosLeft != percentage) {
            expectedPosLeft = percentage;
        }
        percentage = (float) ((argument.getMaxValue() - argument.getMinimum()) / (argument.getMaximum() - argument.getMinimum()));
        if (expectedPosRight != percentage) {
            expectedPosRight = percentage;
        }
        sliderLeft = FunctionUtil.done(expectedPosLeft);
        sliderRight = FunctionUtil.done(expectedPosRight);
    }

    @Override
    public void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        int pureWhite = MathUtil.reAlpha(0xD8D8D8, ClickUI.alphaAnimation.calculate());
        int white = MathUtil.reAlpha(0xFFFFFF, ClickUI.alphaAnimation.calculate());
        int lightWhite = MathUtil.reAlpha(0xFFFFFF, (int) (ClickUI.alphaAnimation.calculate() * 0.1f));
        FontRegistry.GEIST.drawString(graphics, getArgumentName(), 3.5f, x, y, pureWhite, false);
        double min = argument.getMinimum();
        double max = argument.getMaximum();
        if (dragging == 1) {
            double percentage = Math.clamp(Math.min((mouseX - x - 1) / 58.0, expectedPosRight), 0, 1);
            double current = percentage * (max - min) + min;
            argument.setMinValue(current);
        } else if (dragging == 2) {
            double percentage = Math.clamp(Math.max((mouseX - x - 11) / 58.0, expectedPosLeft), 0, 1);
            double current = percentage * (max - min) + min;
            argument.setMaxValue(current);
        }
        float percentage = (float) ((argument.getMinValue() - min) / (max - min));
        if (expectedPosLeft != percentage) {
            expectedPosLeft = percentage;
            sliderLeft = FunctionUtil.linear(sliderLeft.calculate(), expectedPosLeft, 40);
        }
        percentage = (float) ((argument.getMaxValue() - argument.getMinimum()) / (argument.getMaximum() - argument.getMinimum()));
        if (expectedPosRight != percentage) {
            expectedPosRight = percentage;
            sliderRight = FunctionUtil.linear(sliderRight.calculate(), expectedPosRight, 40);
        }
        float xStart = Math.round(sliderLeft.calculate() * 58), xEnd = 10 + Math.round(sliderRight.calculate() * 58);
        int leftColor = MathUtil.reAlpha(Colors.MAIN_THEME, (int) (ClickUI.alphaAnimation.calculate() * 0.7f));
        int rightColor = MathUtil.reAlpha(0x966EEB, ClickUI.alphaAnimation.calculate());
        RenderUtil.drawRoundedGradientHorizontal(graphics, x + 2 + xStart, y + 13, x + 2 + xEnd, y + 15, 1, leftColor, rightColor);
        RenderUtil.drawRoundedRect(graphics, x + 2, y + 13, x + 70, y + 15, 1, lightWhite);
        RenderUtil.drawRoundedRect(graphics, x + xStart - 2, y + 11, x + 4 + xStart, y + 17, 3, white);
        RenderUtil.drawRoundedRect(graphics, x + xEnd - 2, y + 11, x + 4 + xEnd, y + 17, 3, white);
        String text = formatter.format(argument.getMinValue()) + "-" + formatter.format(argument.getMaxValue());
        int bg = MathUtil.reAlpha(Colors.MAIN_THEME, (int) (ClickUI.alphaAnimation.calculate() * 0.18f));
        int textColor = MathUtil.reAlpha(0xD6C7FA, ClickUI.alphaAnimation.calculate());
        float textWidth = FontRegistry.GEIST.width(text, 3.5f);
        RenderUtil.drawRoundedRect(graphics, x + 63 - textWidth, y, x + 70, y + 9, 2, bg);
        FontRegistry.GEIST.drawString(graphics, text, 3.5f, x + 66 - textWidth, y, textColor, false);
    }

    @Override
    public void onMouseClicked(int x, int y, double mouseX, double mouseY) {
        if (MathUtil.isHovered(x - 2, y + 11, x + 86, y + 17, mouseX, mouseY)) {
            float mid = (expectedPosRight + expectedPosLeft) / 2 * 58 + x + 2;
            if (mouseX > mid) dragging = 2;
            else dragging = 1;
        }
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public void onMouseReleased(int mouseButton) {
        dragging = 0;
    }
}
