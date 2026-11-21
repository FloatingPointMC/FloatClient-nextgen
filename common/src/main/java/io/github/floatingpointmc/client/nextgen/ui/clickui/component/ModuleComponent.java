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

package io.github.floatingpointmc.client.nextgen.ui.clickui.component;

import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.argument.Argument;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.CheckBox;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.SelectionList;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.Slider;
import io.github.floatingpointmc.client.nextgen.ui.clickui.ClickUI;
import io.github.floatingpointmc.client.nextgen.ui.clickui.Colors;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.argument.ArgumentComponent;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.argument.impl.CheckBoxComponent;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.argument.impl.SelectionListComponent;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.argument.impl.SliderComponent;
import io.github.floatingpointmc.client.nextgen.ui.font.FontRegistry;
import io.github.floatingpointmc.client.nextgen.util.math.FunctionUtil;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import io.github.floatingpointmc.client.nextgen.util.render.RenderUtil;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ModuleComponent {
    private final Module module;
    private final ArrayList<ArgumentComponent<?>> components;
    @Getter
    private int expectHeight;
    public boolean expanded = false, enabled = false, hovered = false;
    @Getter
    private @NotNull FunctionUtil<Integer> height;
    private @NotNull FunctionUtil<Integer> hint = FunctionUtil.done(0), angle = FunctionUtil.done(-90), textRed, textGreen, textBlue;
    private @NotNull FunctionUtil<Float> argumentAlpha = FunctionUtil.done(0f), enabledMark;

    public ModuleComponent(Module module) {
        this.module = module;
        components = new ArrayList<>();
        for (Argument<?> argument : module.getArguments()) {
            if (argument instanceof CheckBox checkBox) {
                components.add(new CheckBoxComponent(module, checkBox));
            } else if (argument instanceof Slider slider) {
                components.add(new SliderComponent(module, slider));
            } else if (argument instanceof SelectionList selectionList) {
                components.add(new SelectionListComponent(module, selectionList));
            }
        }
        expectHeight = 20;
        height = FunctionUtil.done(20);
        enabledMark = FunctionUtil.done(module.isEnabled() ? 6f : 0f);
        textRed = FunctionUtil.done(module.isEnabled() ? 198 : 150);
        textGreen = FunctionUtil.done(module.isEnabled() ? 172 : 150);
        textBlue = FunctionUtil.done(module.isEnabled() ? 248 : 160);
    }

    public void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        int pureLight = MathUtil.reAlpha(Colors.LIGHT_THEME, ClickUI.alphaAnimation.calculate());
        int pureWhite = MathUtil.reAlpha(0xFFFFFF, ClickUI.alphaAnimation.calculate());
        int pureGrey = MathUtil.reAlpha(0xC3C3C3, ClickUI.alphaAnimation.calculate());
        int nameColor = MathUtil.reAlpha(textRed.calculate() << 16 | textGreen.calculate() << 8 | textBlue.calculate(), ClickUI.alphaAnimation.calculate());
        int mainShadow = MathUtil.reAlpha(Colors.MAIN_THEME, (int) (ClickUI.alphaAnimation.calculate() * 0.55f));
        if (MathUtil.isHovered(x + 4, y, x + 96, y + 19, mouseX, mouseY)) {
            if (!hovered) {
                hint = FunctionUtil.linear(hint.calculate(), (int) (ClickUI.alphaAnimation.calculate() * 0.07f), 50);
                hovered = true;
            }
        } else if (hovered) {
            hint = FunctionUtil.linear(hint.calculate(), 0, 50);
            hovered = false;
        }
        if (enabled ^ module.isEnabled()) {
            enabledMark = FunctionUtil.linear(enabledMark.calculate(), module.isEnabled() ? 6 : 0, 200);
            textRed = FunctionUtil.linear(textRed.calculate(), module.isEnabled() ? 198 : 150, 200);
            textGreen = FunctionUtil.linear(textGreen.calculate(), module.isEnabled() ? 172 : 150, 200);
            textBlue = FunctionUtil.linear(textBlue.calculate(), module.isEnabled() ? 248 : 160, 200);
            enabled = module.isEnabled();
        }
        if (hint.calculate() > 0) {
            RenderUtil.drawRoundedRect(graphics, x + 4, y, x + 96, y + 19, 4, MathUtil.reAlpha(pureWhite, hint.calculate()));
        }
        if (enabledMark.calculate() > 0) {
            RenderUtil.drawRoundedRect(graphics, x + 5, y + 10 - enabledMark.calculate(), x + 7.5f, y + 9 + enabledMark.calculate(), 1, pureLight);
            FontRegistry.GEIST.drawString(graphics, module.getName(), 4f, x + 12.5f, y + 4.5f, mainShadow, false);
        }
        FontRegistry.GEIST.drawString(graphics, module.getName(), 4f, x + 12, y + 4, nameColor, false);
        if (!module.getArguments().isEmpty()) {
            RenderUtil.drawChevron(graphics, x + 85, y + 10, angle.calculate(), pureGrey);
            if (expanded || height.calculate() != 20) {
                int currentY = 28;
                RenderUtil.drawRoundedRect(graphics, x + 10, y + 24, x + 90, y + expectHeight - 5, 5, MathUtil.reAlpha(0x0A0A0C, (int) (argumentAlpha.calculate() * 76.5f)));
                RenderUtil.drawRoundedOutline(graphics, x + 10, y + 24, x + 90, y + expectHeight - 5, 5, 1, MathUtil.reAlpha(0xFFFFFF, (int) (argumentAlpha.calculate() * 12.75f)));
                graphics.enableScissor(x + 4, y, x + 96, y + height.calculate());
                for (ArgumentComponent<?> component : components) {
                    if (component.isDisplayable()) {
                        component.draw(graphics, x + 14, y + currentY, mouseX, mouseY);
                        currentY += component.getHeight();
                    }
                }
                graphics.disableScissor();
            }
        }
    }

    public boolean onMouseClicked(int x, int y, double mouseX, double mouseY, int mouseButton) {
        if (MathUtil.isHovered(x + 4, y, x + 96, y + height.calculate(), mouseX, mouseY)) {
            if (MathUtil.isHovered(x + 4, y, x + 96, y + 19, mouseX, mouseY)) {
                if (mouseButton == 1 && module.isToggleable()) {
                    module.setEnabled(!module.isEnabled());
                } else if (mouseButton == 3 && !module.getArguments().isEmpty()) {
                    expanded = !expanded;
                }
            } else if (expanded && mouseButton == 1) {
                int currentY = 28;
                for (ArgumentComponent<?> component : components) {
                    if (component.isDisplayable()) {
                        component.onMouseClicked(x + 14, y + currentY, mouseX, mouseY);
                        currentY += component.getHeight();
                    }
                }
            }
        }
        return false;
    }

    public void onMouseReleased(int mouseButton) {
        components.forEach(component -> component.onMouseReleased(mouseButton));
    }


    public void tick() {
        int currentY = 20;
        if (expanded) {
            currentY += 8;
            for (ArgumentComponent<?> component : components) {
                if (component.isDisplayable()) currentY += component.getHeight();
            }
            currentY += 5;
        }
        if (expectHeight != currentY) {
            expectHeight = currentY;
            height = FunctionUtil.easeOutQuad(height.calculate(), expectHeight, 200);
            angle = FunctionUtil.linear(angle.calculate(), expanded ? 90 : -90, 200);
            argumentAlpha = FunctionUtil.linear(argumentAlpha.calculate(), expanded ? 1f : 0f, 200);
        }
    }
}
