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
import org.lanternpowered.server.scoreboard.LanternCollisionRule;
import org.spongepowered.api.scoreboard.CollisionRule;
import org.spongepowered.api.scoreboard.CollisionRules;

public final class CollisionRuleRegistryModule extends AdditionalPluginCatalogRegistryModule<CollisionRule> {

    public CollisionRuleRegistryModule() {
        super(CollisionRules.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternCollisionRule(CatalogKeys.minecraft("always")));
        register(new LanternCollisionRule(CatalogKeys.minecraft("push_own_team", "pushOwnTeam")));
        register(new LanternCollisionRule(CatalogKeys.minecraft("push_other_teams", "pushOtherTeams")));
        register(new LanternCollisionRule(CatalogKeys.minecraft("never")));
    }
}
