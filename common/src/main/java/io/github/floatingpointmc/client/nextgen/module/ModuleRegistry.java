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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.floatingpointmc.client.nextgen.config.ConfigManager;
import io.github.floatingpointmc.client.nextgen.event.impl.client.KeyEvent;
import io.github.floatingpointmc.client.nextgen.event.impl.client.TickEvent;
import io.github.floatingpointmc.client.nextgen.module.argument.Argument;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.CheckBox;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.SelectionList;
import io.github.floatingpointmc.client.nextgen.module.argument.impl.Slider;
import io.github.floatingpointmc.client.nextgen.module.impl.combat.AntiBot;
import io.github.floatingpointmc.client.nextgen.module.impl.combat.KillAura;
import io.github.floatingpointmc.client.nextgen.module.impl.misc.Target;
import io.github.floatingpointmc.client.nextgen.module.impl.render.ClickGUI;
import io.github.floatingpointmc.client.nextgen.module.impl.render.HUD;
import io.github.floatingpointmc.client.nextgen.module.impl.render.ModuleList;
import io.github.floatingpointmc.client.nextgen.module.impl.render.Rotation;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import io.github.vlouboos.standaloneevent.api.StandaloneEventAPI;
import it.unimi.dsi.fastutil.objects.*;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Optional;

@Log4j2
public class ModuleRegistry {
    private static final @NotNull Object2ReferenceMap<String, Module> modules;
    private static final @NotNull Object2ReferenceMap<Category, ReferenceSet<Module>> modulesByCategory;
    private static final ArrayDeque<Module> enabledModules = new ArrayDeque<>();
    private static boolean loaded;

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
                new AntiBot(),
                new KillAura()
        );
        add(implementations, renderImplementations,
                new ClickGUI(),
                new HUD(),
                new ModuleList(),
                new Rotation()
        );
        add(implementations, miscImplementations,
                new Target()
        );
        implementationsByCategory.put(Category.COMBAT, ReferenceSets.unmodifiable(combatImplementations));
        implementationsByCategory.put(Category.MOVEMENT, ReferenceSets.unmodifiable(movementImplementations));
        implementationsByCategory.put(Category.RENDER, ReferenceSets.unmodifiable(renderImplementations));
        implementationsByCategory.put(Category.PLAYER, ReferenceSets.unmodifiable(playerImplementations));
        implementationsByCategory.put(Category.WORLD, ReferenceSets.unmodifiable(worldImplementations));
        implementationsByCategory.put(Category.MISC, ReferenceSets.unmodifiable(miscImplementations));

        modulesByCategory = Object2ReferenceMaps.unmodifiable(implementationsByCategory);
        modules = Object2ReferenceMaps.unmodifiable(implementations);
        StandaloneEventAPI.getApi().register(ModuleRegistry.class);
    }

    public static void init() {
        JsonObject json = ConfigManager.readConfig("module");
        for (Module module : modules.values()) {
            if (json.has(module.getIdentity())) {
                JsonElement moduleElement = json.getAsJsonObject(module.getIdentity());
                if (moduleElement.isJsonObject()) {
                    JsonObject moduleJson = moduleElement.getAsJsonObject();
                    if (moduleJson.has("enabled") && moduleJson.get("enabled").isJsonPrimitive()) {
                        if (moduleJson.get("enabled").getAsBoolean()) enabledModules.add(module);
                        else enabledModules.remove(module);
                    }
                    if (moduleJson.has("key") && moduleJson.get("key").isJsonPrimitive() && moduleJson.get("key").getAsJsonPrimitive().isNumber()) {
                        module.setKey(moduleJson.get("key").getAsInt());
                    }
                    if (moduleJson.has("arguments") && moduleJson.get("arguments").isJsonObject()) {
                        JsonObject argumentsJson = moduleJson.getAsJsonObject("arguments");
                        for (Argument<?> argument : module.getArguments()) {
                            if (argumentsJson.has(argument.identity) && argumentsJson.get(argument.identity).isJsonPrimitive()) {
                                JsonPrimitive argumentJson = argumentsJson.getAsJsonPrimitive(argument.identity);
                                switch (argument) {
                                    case CheckBox checkBox when argumentJson.isBoolean() ->
                                            checkBox.setValue(argumentJson.getAsBoolean());
                                    case Slider slider when argumentJson.isNumber() ->
                                            slider.setValue(argumentJson.getAsDouble());
                                    case SelectionList selectionList when argumentJson.isString() ->
                                            selectionList.setValue(argumentJson.getAsString());
                                    default -> {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        log.info("Loaded {} modules.", modules.size());
    }

    public static void shutdown() {
        JsonObject json = new JsonObject();
        for (Module module : modules.values()) {
            JsonObject moduleJson = new JsonObject();
            moduleJson.addProperty("enabled", module.isEnabled());
            moduleJson.addProperty("key", module.getKey());
            if (!module.getArguments().isEmpty()) {
                JsonObject argumentsJson = new JsonObject();
                for (Argument<?> argument : module.getArguments()) {
                    if (argument instanceof CheckBox checkBox) {
                        argumentsJson.addProperty(checkBox.identity, checkBox.getValue());
                    } else if (argument instanceof Slider slider) {
                        argumentsJson.addProperty(slider.identity, slider.getValue());
                    } else if (argument instanceof SelectionList selectionList) {
                        argumentsJson.addProperty(selectionList.identity, selectionList.getValue());
                    }
                }
                moduleJson.add("arguments", argumentsJson);
            }
            json.add(module.getIdentity(), moduleJson);
        }
        ConfigManager.writeConfig("module", json);
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

    public static @NotNull Optional<Module> get(@NotNull String identity) {
        return Optional.ofNullable(modules.get(identity));
    }

    @EventHandler
    public static void onKey(@NotNull KeyEvent e) {
        for (Module module : modules.values()) {
            if (module.getKey() == e.key) module.toggle();
        }
    }

    @EventHandler
    public static void onTick(@NotNull TickEvent e) {
        if (Minecraft.getInstance().player == null) return;
        if (!loaded) {
            while (!enabledModules.isEmpty()) {
                enabledModules.poll().setEnabled(true, false);
            }
            loaded = true;
        }
    }

    public static void enable(@NotNull Module module) {
        enabledModules.add(module);
    }

    public static ReferenceCollection<Module> all() {
        return ReferenceCollections.unmodifiable(modules.values());
    }
}
