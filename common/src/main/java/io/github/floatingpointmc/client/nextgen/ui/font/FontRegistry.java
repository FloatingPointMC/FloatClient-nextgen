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

import io.github.floatingpointmc.client.nextgen.FloatClientNextgen;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@Log4j2
public final class FontRegistry {
    public static final Font GEIST;
    public static final Font GEIST_BOLD;

    static {
        GEIST = font("font/geist.ttf");
        GEIST_BOLD = font("font/geist-bold.ttf");
    }

    private static @NotNull Font font(@NotNull String path) {
        return new Font(Identifier.fromNamespaceAndPath(FloatClientNextgen.MOD_ID, path));
    }


    public static void init() {
        log.info("Loaded {} fonts.", 2);
    }
}
