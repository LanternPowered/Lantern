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
import org.lanternpowered.server.scoreboard.LanternCriterion;
import org.spongepowered.api.scoreboard.criteria.Criteria;
import org.spongepowered.api.scoreboard.criteria.Criterion;

public final class CriterionRegistryModule extends AdditionalPluginCatalogRegistryModule<Criterion> {

    public CriterionRegistryModule() {
        super(Criteria.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternCriterion(CatalogKeys.minecraft("dummy")));
        register(new LanternCriterion(CatalogKeys.minecraft("trigger")));
        register(new LanternCriterion(CatalogKeys.minecraft("health")));
        register(new LanternCriterion(CatalogKeys.minecraft("player_kills", "playerKillCount")));
        register(new LanternCriterion(CatalogKeys.minecraft("total_kills", "totalKillCount")));
        register(new LanternCriterion(CatalogKeys.minecraft("deaths", "deathCount")));
    }
}
