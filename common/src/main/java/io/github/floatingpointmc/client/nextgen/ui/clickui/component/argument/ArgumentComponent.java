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

package io.github.floatingpointmc.client.nextgen.ui.clickui.component.argument;

import io.github.floatingpointmc.client.nextgen.language.LanguageManager;
import io.github.floatingpointmc.client.nextgen.module.Module;
import io.github.floatingpointmc.client.nextgen.module.argument.Argument;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public abstract class ArgumentComponent<T extends Argument<?>> {
    protected final @NotNull Module parent;
    protected final @NotNull T argument;

    public abstract void draw(@NotNull GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY);

    public abstract void onMouseClicked(int x, int y, double mouseX, double mouseY);

    public abstract int getHeight();

    public final boolean isDisplayable() {
        return argument.isDisplayable();
    }

    protected final String getArgumentName() {
        return LanguageManager.get("module.%s.%s".formatted(parent.getIdentity(), argument.identity));
    }

    protected final String getArgumentDescription() {
        return LanguageManager.get("module.%s.%s.desc".formatted(parent.getIdentity(), argument.identity));
    }

    public void onMouseReleased(int mouseButton) {
    }
}
