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

package io.github.floatingpointmc.client.nextgen.ui.clickui;

import io.github.floatingpointmc.client.nextgen.module.Category;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.CategoryComponent;
import io.github.floatingpointmc.client.nextgen.ui.clickui.component.TopLayer;
import io.github.floatingpointmc.client.nextgen.util.math.FunctionUtil;
import io.github.floatingpointmc.client.nextgen.util.math.MathUtil;
import io.github.floatingpointmc.client.nextgen.util.render.BlurUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ClickUI extends Screen {
    private static final @NotNull ClickUI INSTANCE;
    private static final List<CategoryComponent> categories;
    public static @Nullable TopLayer topLayer = null;
    public static @NotNull FunctionUtil<Integer> alphaAnimation = FunctionUtil.done(0);

    static {
        int x = 50, y = 50;
        categories = new ArrayList<>();
        for (Category category : Category.values()) {
            categories.add(new CategoryComponent(category, x, y));
            x += 140;
        }
        INSTANCE = new ClickUI();
    }

    public ClickUI() {
        super(Component.literal("floatclient_clickui"));
    }

    public static @NotNull ClickUI INSTANCE() {
        return INSTANCE;
    }

    @Override
    protected void init() {
        alphaAnimation = FunctionUtil.linear(0, 255, 100);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int background = MathUtil.reAlpha(0xFF100F16, (int) (alphaAnimation.calculate() * 0.52f));
        BlurUtil.blurAll(graphics, 2);
        graphics.fill(0, 0, width, height, background);
        categories.forEach(categoryComponent -> categoryComponent.draw(graphics, mouseX, mouseY));
        if (topLayer != null) {
            topLayer.draw(graphics, mouseX, mouseY);
            if (topLayer.finished()) topLayer = null;
        }
    }

    // who the fuck is using double here while using int when rendering???
    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x(), mouseY = event.y();
        int mouseButton = event.button();
        if (topLayer != null && topLayer.onMouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        for (CategoryComponent categoryComponent : categories) {
            if (categoryComponent.onMouseClicked(mouseX, mouseY, mouseButton)) return true;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent event) {
        int mouseButton = event.button();
        categories.forEach(categoryComponent -> categoryComponent.onMouseReleased(mouseButton));
        return true;
    }

    @Override
    public void tick() {
        categories.forEach(CategoryComponent::tick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
