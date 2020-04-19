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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.LanternPickupRule;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.PickupRule;
import org.spongepowered.api.data.type.PickupRules;

public class PickupRuleRegistryModule extends InternalPluginCatalogRegistryModule<PickupRule> {

    private static final PickupRuleRegistryModule INSTANCE = new PickupRuleRegistryModule();

    public static PickupRuleRegistryModule get() {
        return INSTANCE;
    }

    private PickupRuleRegistryModule() {
        super(PickupRules.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternPickupRule(CatalogKey.minecraft("disallowed"), 0));
        register(new LanternPickupRule(CatalogKey.minecraft("allowed"), 1));
        register(new LanternPickupRule(CatalogKey.minecraft("creative_only"), 2));
    }
}
