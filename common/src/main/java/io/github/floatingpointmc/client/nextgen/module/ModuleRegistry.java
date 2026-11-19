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

import io.github.floatingpointmc.client.nextgen.event.impl.client.KeyEvent;
import io.github.floatingpointmc.client.nextgen.module.impl.combat.AntiBot;
import io.github.floatingpointmc.client.nextgen.module.impl.render.ClickGUI;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import io.github.vlouboos.standaloneevent.api.StandaloneEventAPI;
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
        var movementImplementations = new ReferenceLinkedOpenHashSet<Module>();
        var renderImplementations = new ReferenceLinkedOpenHashSet<Module>();
        var playerImplementations = new ReferenceLinkedOpenHashSet<Module>();
        var worldImplementations = new ReferenceLinkedOpenHashSet<Module>();
        var miscImplementations = new ReferenceLinkedOpenHashSet<Module>();
        add(implementations, combatImplementations,
                new AntiBot()
        );
        add(implementations, renderImplementations,
                new ClickGUI()
        );
        implementationsByCategory.put(Category.COMBAT, ReferenceSets.unmodifiable(combatImplementations));

        modulesByCategory = Object2ReferenceMaps.unmodifiable(implementationsByCategory);
        modules = Object2ReferenceMaps.unmodifiable(implementations);
        StandaloneEventAPI.getApi().register(ModuleRegistry.class);
    }

    public static void init() {
        log.info("Loaded {} modules.", modules.size());
    }

    private static void add(@NotNull Object2ReferenceLinkedOpenHashMap<String, Module> implementations, @NotNull ReferenceLinkedOpenHashSet<Module> implementationsInCategory, @NotNull Module @NotNull ... modules) {
        for (Module module : modules) {
            implementations.put(module.getIdentity(), module);
            implementationsInCategory.add(module);
        }
    }

    public static @NotNull ReferenceSet<Module> getByCategory(@NotNull Category category) {
        return modulesByCategory.get(category);
    }

    @EventHandler
    public static void onKey(KeyEvent e) {
        for (Module module : modules.values()) {
            if (module.getKey() == e.key) module.toggle();
        }
    }
}
