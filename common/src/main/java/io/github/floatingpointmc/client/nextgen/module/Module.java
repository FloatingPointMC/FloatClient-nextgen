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

package io.github.floatingpointmc.client.nextgen.module;

import io.github.floatingpointmc.client.nextgen.language.LanguageManager;
import io.github.floatingpointmc.client.nextgen.module.argument.Argument;
import io.github.floatingpointmc.client.nextgen.util.container.Timer;
import io.github.vlouboos.standaloneevent.api.StandaloneEventAPI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Module {
    private static final Timer SOUND_TIMER = new Timer();
    protected final Minecraft mc = Minecraft.getInstance();
    @Getter
    private final @NotNull String identity;
    @Getter
    private final @NotNull Category category;
    @Getter
    private final boolean toggleable;
    @Getter
    private final @NotNull ArrayList<Argument<?>> arguments = new ArrayList<>();
    @Getter
    private boolean enabled = false;
    @Getter
    @Setter
    private int key = 0;

    public Module(@NotNull String identity, @NotNull Category category) {
        this(identity, category, true);
    }

    protected void addArguments(@NotNull Argument<?> @NotNull ... argument) {
        arguments.addAll(List.of(argument));
    }

    protected void onEnable() {}

    protected void onDisable() {}

    public @Nullable Argument<?> getSuffix() {
        return null;
    }

    public void setEnabled(boolean enabled) {
        setEnabled(enabled, true);
    }

    public void setEnabled(boolean enabled, boolean withSound) {
        assert mc.player != null;
        if (!toggleable) {
            onEnable();
            return;
        }
        if (enabled && !this.enabled) {
            this.enabled = true;
            onEnable();
            StandaloneEventAPI.getApi().register(this);
            if (withSound && SOUND_TIMER.isComplete(100)) {
                mc.player.playSound(SoundEvents.LEVER_CLICK, 0.5F, 1.0F);
                SOUND_TIMER.reset();
            }
        } else if (!enabled && this.enabled) {
            StandaloneEventAPI.getApi().unregister(this);
            onDisable();
            this.enabled = false;
            if (withSound && SOUND_TIMER.isComplete(100)) {
                mc.player.playSound(SoundEvents.LEVER_CLICK, 0.4F, 0.8F);
                SOUND_TIMER.reset();
            }
        }
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public @NotNull String getName() {
        return LanguageManager.get("module." + identity);
    }
}
