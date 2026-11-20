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
import io.github.floatingpointmc.client.nextgen.ui.font.Fonts;
import io.github.floatingpointmc.client.nextgen.util.math.FunctionUtil;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import io.github.floatingpointmc.client.nextgen.util.render.BlurUtil;
import io.github.floatingpointmc.client.nextgen.util.render.RenderUtil;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

public final class CategoryComponent {
    private final Category category;
    private final ReferenceArrayList<ModuleComponent> moduleComponents;
    private int x, y;
    private int expectHeight;
    private boolean dragging = false;
    private FunctionUtil<Integer> activeHeight;

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
        int bottom = y + currentY;
        BlurUtil.bloomKawase(graphics, 2, 3,
                capture -> capture.roundedRect(x, y, x + 100, bottom, 10, pureExtremeDark));
        RenderUtil.drawRoundedRect(graphics, x, y, x + 100, y + currentY, 10, background);
        RenderUtil.drawRoundedOutline(graphics, x, y, x + 100, y + currentY, 10, 1, lightWhite);
        graphics.text(Minecraft.getInstance().font, Component.literal(category.getName()).withStyle(Style.EMPTY.withFont(Fonts.GEIST_BOLD)), x + 26, y + 10, pureWhite);

    }

    public boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }
}
