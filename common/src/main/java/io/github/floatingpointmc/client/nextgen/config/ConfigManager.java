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

package io.github.floatingpointmc.client.nextgen.config;

import com.google.gson.JsonObject;
import io.github.floatingpointmc.client.nextgen.util.io.IOUtil;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Log4j2
public class ConfigManager {
    private static final File dir;

    static {
        dir = new File(Minecraft.getInstance().gameDirectory, "float-nextgen");
    }

    public static void init() {
        if (!dir.exists() && !dir.mkdir()) {
            throw new RuntimeException("Failed to create float-nextgen directory");
        }
        if (!dir.isDirectory() && !dir.delete() && !dir.mkdir()) {
            throw new RuntimeException("Failed to delete and create float-nextgen directory");
        }
        log.info("Root directory is at `{}`.", dir.getAbsolutePath());
    }

    public static @NotNull JsonObject readConfig(@NotNull String name) {
        File file = new File(dir, name + ".json");
        if (!file.exists()) {
            return new JsonObject();
        }
        return IOUtil.readFileToJson(file);
    }

    public static void writeConfig(@NotNull String name, @NotNull JsonObject json) {
        File file = new File(dir, name + ".json");
        IOUtil.writeFileFromJson(file, json);
    }
}
