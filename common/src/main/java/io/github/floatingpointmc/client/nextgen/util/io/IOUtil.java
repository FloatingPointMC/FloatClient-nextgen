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

package io.github.floatingpointmc.client.nextgen.util.io;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.*;

@UtilityClass
public class IOUtil {
    public static JsonObject readFileToJson(@NotNull File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return JsonParser.parseString(new String(toByte(fis))).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] toByte(@NotNull InputStream in) throws IOException {
        byte[] bytes = new byte[in.available()];
        if (in.read(bytes) < 0) return new byte[0];
        return bytes;
    }

    @SuppressWarnings("all")
    public static void writeFileFromJson(File file, @NotNull JsonObject json) {
        try (FileWriter fw = new FileWriter(file, false)) {
            fw.write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
