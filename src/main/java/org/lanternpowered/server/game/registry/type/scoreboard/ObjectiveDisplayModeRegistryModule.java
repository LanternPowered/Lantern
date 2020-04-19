/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.game.registry.type.scoreboard;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.scoreboard.LanternObjectiveDisplayMode;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;

public final class ObjectiveDisplayModeRegistryModule extends AdditionalPluginCatalogRegistryModule<ObjectiveDisplayMode> {

    public ObjectiveDisplayModeRegistryModule() {
        super(ObjectiveDisplayModes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternObjectiveDisplayMode(CatalogKey.minecraft("integer"), 0));
        register(new LanternObjectiveDisplayMode(CatalogKey.minecraft("hearts"), 1));
    }
}
