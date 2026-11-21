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

package io.github.floatingpointmc.client.nextgen;

import io.github.floatingpointmc.client.nextgen.command.CommandRegistry;
import io.github.floatingpointmc.client.nextgen.language.LanguageManager;
import io.github.floatingpointmc.client.nextgen.module.ModuleRegistry;
import io.github.floatingpointmc.client.nextgen.ui.font.FontRegistry;
import io.github.vlouboos.standaloneevent.api.ApiProvider;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.gui.font.FontManager;

@Log4j2
public final class FloatClientNextgen {
    public static final String NAME = "Float";
    public static final String EDITION = "Nextgen-";
    public static final String VERSION = "0.0.1-SNAPSHOT";
    public static final String MOD_ID = "floatclient_nextgen";

    public static void init() {
        log.info("Loading event api...");
        long currentMillis = System.currentTimeMillis();
        ApiProvider.injectApi(false);
        log.info("Finished in {}ms.", System.currentTimeMillis() - currentMillis);
        log.info("Loading modules...");
        currentMillis = System.currentTimeMillis();
        ModuleRegistry.init();
        log.info("Finished in {}ms.", System.currentTimeMillis() - currentMillis);
        log.info("Loading commands...");
        currentMillis = System.currentTimeMillis();
        CommandRegistry.init();
        log.info("Finished in {}ms.", System.currentTimeMillis() - currentMillis);
        log.info("Loading config...");
        currentMillis = System.currentTimeMillis();
        log.info("Finished in {}ms.", System.currentTimeMillis() - currentMillis);
        log.info("Loading languages...");
        currentMillis = System.currentTimeMillis();
        LanguageManager.init();
        log.info("Finished in {}ms.", System.currentTimeMillis() - currentMillis);
        log.info("Loading fonts...");
        currentMillis = System.currentTimeMillis();
        FontRegistry.init();
        log.info("Finished in {}ms.", System.currentTimeMillis() - currentMillis);
    }
}
