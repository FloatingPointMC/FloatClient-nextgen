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
import io.github.floatingpointmc.client.nextgen.module.argument.impl.CheckBox;
import io.github.floatingpointmc.client.nextgen.ui.clickui.ClickUI;
import io.github.floatingpointmc.client.nextgen.ui.clickui.Colors;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.argument.ArgumentComponent;
import io.github.floatingpointmc.client.nextgen.ui.font.FontRegistry;
import io.github.floatingpointmc.client.nextgen.util.math.FunctionUtil;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import io.github.floatingpointmc.client.nextgen.util.render.RenderUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

public class CheckBoxComponent extends ArgumentComponent<CheckBox> {
    private @NotNull FunctionUtil<Integer> offset;
    private @NotNull FunctionUtil<Integer> textRed, textGreen, textBlue, bgRed, bgGreen, bgBlue;

    public CheckBoxComponent(@NotNull Module parent, @NotNull CheckBox checkBox) {
        super(parent, checkBox);
        offset = FunctionUtil.done(argument.getValue() ? 5 : 0);
        textRed = FunctionUtil.done(argument.getValue() ? 186 : 150);
        textGreen = FunctionUtil.done(argument.getValue() ? 156 : 150);
        textBlue = FunctionUtil.done(argument.getValue() ? 245 : 160);
        bgRed = FunctionUtil.done(argument.getValue() ? 112 : 16);
        bgGreen = FunctionUtil.done(argument.getValue() ? 74 : 16);
        bgBlue = FunctionUtil.done(argument.getValue() ? 189 : 17);
    }

    @Override
    public void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        int pureWhite = MathUtil.reAlpha(0xFFFFFF, ClickUI.alphaAnimation.calculate());
        int textColor = 0xFF000000 | textRed.calculate() << 16 | textGreen.calculate() << 8 | textBlue.calculate();
        int bgColor = 0xFF000000 | bgRed.calculate() << 16 | bgGreen.calculate() << 8 | bgBlue.calculate();
        if (argument.getValue())
            FontRegistry.GEIST.drawString(graphics, getArgumentName(), 3.5f, x + 0.5f, y + 1.5f,
                    MathUtil.reAlpha(Colors.MAIN_THEME, (int) (ClickUI.alphaAnimation.calculate() * 0.55f)), false);
        FontRegistry.GEIST.drawString(graphics, getArgumentName(), 3.5f, x, y + 1, textColor, false);
        RenderUtil.drawRoundedOutline(graphics, x + 59, y + 2, x + 72, y + 10, 3.8f, 1, textColor);
        RenderUtil.drawRoundedRect(graphics, x + 59.6f, y + 2.6f, x + 71.4f, y + 9.4f, 3, bgColor);
        RenderUtil.drawRoundedRect(graphics, x + 60 + offset.calculate(), y + 3, x + 66 + offset.calculate(), y + 9, 3, pureWhite);
    }

    @Override
    public void onMouseClicked(int x, int y, double mouseX, double mouseY) {
        if (MathUtil.isHovered(x + 59, y + 2, x + 72, y + 10, mouseX, mouseY)) {
            argument.setValue(!argument.getValue());
            offset = FunctionUtil.linear(offset.calculate(), argument.getValue() ? 5 : 0, 150);
            textRed = FunctionUtil.linear(textRed.calculate(), argument.getValue() ? 186 : 150, 150);
            textGreen = FunctionUtil.linear(textGreen.calculate(), argument.getValue() ? 156 : 150, 150);
            textBlue = FunctionUtil.linear(textBlue.calculate(), argument.getValue() ? 245 : 160, 150);
            bgRed = FunctionUtil.linear(bgRed.calculate(), argument.getValue() ? 112 : 16, 150);
            bgGreen = FunctionUtil.linear(bgGreen.calculate(), argument.getValue() ? 74 : 16, 150);
            bgBlue = FunctionUtil.linear(bgBlue.calculate(), argument.getValue() ? 189 : 17, 150);
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }
}
