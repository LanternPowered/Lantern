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

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.scoreboard.LanternVisibility;
import org.spongepowered.api.scoreboard.Visibilities;
import org.spongepowered.api.scoreboard.Visibility;

public final class VisibilityRegistryModule extends AdditionalPluginCatalogRegistryModule<Visibility> {

    public VisibilityRegistryModule() {
        super(Visibilities.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternVisibility(CatalogKeys.minecraft("always")));
        register(new LanternVisibility(CatalogKeys.minecraft("hide_for_own_team", "hideForOwnTeam")));
        register(new LanternVisibility(CatalogKeys.minecraft("hide_for_other_teams", "hideForOtherTeams")));
        register(new LanternVisibility(CatalogKeys.minecraft("never")));
    }
}
