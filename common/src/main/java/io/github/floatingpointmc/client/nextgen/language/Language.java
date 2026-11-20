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

package io.github.floatingpointmc.client.nextgen.language;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public final class Language {
    public final String identity;
    private final Reference2ReferenceMap<String, String> map;

    public Language(String identity) {
        this.identity = identity;
        map = loadLanguage(identity);
    }

    public @NotNull String get(@NotNull String key) {
        return map.getOrDefault(key, key);
    }

    private Reference2ReferenceMap<String, String> loadLanguage(@NotNull String identity) {
        var res = new Reference2ReferenceOpenHashMap<String, String>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Language.class.getResourceAsStream("/assets/floatclient-nextgen/lang/" + identity + ".lang"))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int idx = line.indexOf('=');
                if (idx != -1) {
                    res.put(line.substring(0, idx), line.substring(idx + 1));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Reference2ReferenceMaps.unmodifiable(res);
    }
}
