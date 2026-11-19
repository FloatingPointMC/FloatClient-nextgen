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

import io.github.floatingpointmc.client.nextgen.module.impl.combat.AntiBot;
import it.unimi.dsi.fastutil.objects.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class ModuleRegistry {
    private static final @NotNull Object2ReferenceMap<String, Module> modules;
    private static final @NotNull Object2ReferenceMap<Category, ReferenceSet<Module>> modulesByCategory;

    static {
        var implementations = new Object2ReferenceLinkedOpenHashMap<String, Module>();
        var implementationsByCategory = new Object2ReferenceLinkedOpenHashMap<Category, ReferenceSet<Module>>();
        var combatImplementations = new ReferenceLinkedOpenHashSet<Module>();
        add(implementations, combatImplementations,
                new AntiBot()
        );
        implementationsByCategory.put(Category.COMBAT, ReferenceSets.unmodifiable(combatImplementations));
        modulesByCategory = Object2ReferenceMaps.unmodifiable(implementationsByCategory);
        modules = Object2ReferenceMaps.unmodifiable(implementations);
    }

    public static void init() {
        log.info("Loaded {} modules.", modules.size());
    }

    private static void add(@NotNull Object2ReferenceLinkedOpenHashMap<String, Module> implementations, @NotNull ReferenceLinkedOpenHashSet<Module> implementationsInCategory, @NotNull Module @NotNull ... modules) {

    }

    public static @NotNull ReferenceSet<Module> getByCategory(@NotNull Category category) {
        return modulesByCategory.get(category);
    }
}
