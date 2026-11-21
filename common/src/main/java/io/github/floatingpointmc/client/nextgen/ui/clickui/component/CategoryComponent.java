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

import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.ModuleRegistry;
import io.github.floatingpointmc.client.nextgen.ui.clickui.ClickUI;
import io.github.floatingpointmc.client.nextgen.ui.clickui.Colors;
import io.github.floatingpointmc.client.nextgen.ui.font.FontRegistry;
import io.github.floatingpointmc.client.nextgen.util.math.FunctionUtil;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import io.github.floatingpointmc.client.nextgen.util.render.BlurUtil;
import io.github.floatingpointmc.client.nextgen.util.render.RenderUtil;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class CategoryComponent {
    private final Category category;
    private final ReferenceArrayList<ModuleComponent> moduleComponents;
    private int x, y;
    private int expectHeight;
    private boolean dragging = false;
    private FunctionUtil<Integer> activeHeight;
    private int prevMouseX = -1, prevMouseY = -1;

    public CategoryComponent(Category category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
        moduleComponents = new ReferenceArrayList<>();
        ReferenceSet<Module> modules = ModuleRegistry.getByCategory(category);
        modules.forEach(module -> moduleComponents.add(new ModuleComponent(module)));
        expectHeight = 20 * modules.size();
        activeHeight = FunctionUtil.done(expectHeight);
    }

    public void draw(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (dragging) {
            x += mouseX - prevMouseX;
            y += mouseY - prevMouseY;
        }
        prevMouseX = mouseX;
        prevMouseY = mouseY;
        int pureWhite = MathUtil.reAlpha(0xD8D8D8, ClickUI.alphaAnimation.calculate());
        int lightWhite = MathUtil.reAlpha(0xFFFFFF, (int) (ClickUI.alphaAnimation.calculate() * 0.06f));
        int pureLight = MathUtil.reAlpha(Colors.LIGHT_THEME, ClickUI.alphaAnimation.calculate());
        int pureDark = MathUtil.reAlpha(Colors.DARK_THEME, ClickUI.alphaAnimation.calculate());
        int shadow = MathUtil.reAlpha(Colors.MAIN_THEME, (int) (ClickUI.alphaAnimation.calculate() * 0.22f));
        int shadowBottom = MathUtil.reAlpha(Colors.MAIN_THEME, (int) (ClickUI.alphaAnimation.calculate() * 0.04f));
        int pureExtremeDark = MathUtil.reAlpha(Colors.EXTREME_DARK_THEME, (int) (ClickUI.alphaAnimation.calculate() * 0.88f));
        int background = MathUtil.reAlpha(0xFF100F16, (int) (ClickUI.alphaAnimation.calculate() * 0.72f));
        int currentY = 28;
        for (ModuleComponent moduleComponent : moduleComponents) {
            currentY += moduleComponent.getHeight().calculate();
        }
        currentY += 4;
        int bottom = y + currentY;
        BlurUtil.bloomKawase(graphics, 2, 3, RenderUtil.roundedRect(graphics, x, y, x + 100, bottom, 10, pureExtremeDark));
        RenderUtil.drawRoundedRect(graphics, x, y, x + 100, y + currentY, 10, background);

        RenderUtil.drawRoundedOutline(graphics, x, y, x + 100, y + currentY, 10, 1, lightWhite);
        RenderUtil.drawRoundedGradientRect(graphics, x, y, x + 100, y + 14, 10, 14, shadow, shadowBottom);

        BlurUtil.bloomKawase(graphics, 2, 1, RenderUtil.roundedRect(graphics, x + 9, y + 7, x + 22, y + 20, 4.5f, MathUtil.reAlpha(Colors.MAIN_THEME, (int) (ClickUI.alphaAnimation.calculate() * 0.5f))));
        RenderUtil.drawRoundedGradientRect(graphics, x + 9, y + 7, x + 22, y + 20, 4.5f, pureLight, pureDark);

        BlurUtil.bloomKawase(graphics, 1, 1, FontRegistry.GEIST_BOLD.text(graphics, category.getName(), 4.2f, x + 26, y + 8, MathUtil.reAlpha(pureWhite, (int) (ClickUI.alphaAnimation.calculate() * 0.8f)), false));
        FontRegistry.GEIST_BOLD.drawString(graphics, category.getName(), 4.2f, x + 26, y + 8, pureWhite, false);

        RenderUtil.drawImage(graphics, Identifier.fromNamespaceAndPath("floatclient_nextgen", "clickui/" + category.name().toLowerCase() + ".png"), x + 12, y + 10, 7, 7);
        graphics.fill(x + 1, y + 24, x + 99, y + 25, lightWhite);
        currentY = 28;
        for (ModuleComponent moduleComponent : moduleComponents) {
            moduleComponent.draw(graphics, x, y + currentY, mouseX, mouseY);
            currentY += moduleComponent.getHeight().calculate();
        }
    }

    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (MathUtil.isHovered(x, y, x + 100, y + 24, mouseX, mouseY)) {
            if (mouseButton == 1) {
                dragging = true;
                return true;
            }
        } else if (MathUtil.isHovered(x + 4, y + 25, x + 100, y + 28 + activeHeight.calculate(), mouseX, mouseY)) {
            int currentY = 28;
            for (ModuleComponent moduleComponent : moduleComponents) {
                if (moduleComponent.onMouseClicked(x, y + currentY, mouseX, mouseY, mouseButton)) {
                    return true;
                }
                currentY += moduleComponent.getHeight().calculate();
            }
        }
        return false;
    }

    public void onMouseReleased(int mouseButton) {
        if (mouseButton == 1) dragging = false;
        moduleComponents.forEach(moduleComponent -> moduleComponent.onMouseReleased(mouseButton));
    }

    public void tick() {
        int height = 0;
        for (ModuleComponent moduleComponent : moduleComponents) {
            moduleComponent.tick();
            height += moduleComponent.getExpectHeight();
        }
        if (expectHeight != height) {
            activeHeight = FunctionUtil.easeOutQuad(activeHeight == null ? 0 : activeHeight.calculate(), height, 150);
            expectHeight = height;
        }
    }
}
