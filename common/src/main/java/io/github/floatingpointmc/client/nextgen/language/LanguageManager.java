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
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public final class LanguageManager {
    private static final Reference2ReferenceMap<String, Language> langMap;
    private static String currentLanguage = "en_US";

    static {
        var map = new Reference2ReferenceOpenHashMap<String, Language>();
        for (String language : new String[]{"en_US"}) {
            map.put(language, new Language(language));
        }
        langMap = Reference2ReferenceMaps.unmodifiable(map);
    }

    public static void init() {
        log.info("Loaded {} languages.", langMap.size());
    }

    public static @NotNull String get(@NotNull String key) {
        return langMap.get(currentLanguage).get(key);
    }

    public static void setCurrentLanguage(@NotNull String language) {
        if (langMap.containsKey(language)) {
            currentLanguage = language;
        }
    }
}
