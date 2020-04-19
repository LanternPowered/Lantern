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
package org.lanternpowered.server.game.registry.type.world;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.world.portal.EmptyPortalAgent;
import org.lanternpowered.server.world.portal.LanternPortalAgentType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.world.teleport.PortalAgentType;
import org.spongepowered.api.world.teleport.PortalAgentTypes;

public class PortalAgentTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<PortalAgentType> {

    public PortalAgentTypeRegistryModule() {
        super(PortalAgentTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternPortalAgentType<>(CatalogKey.minecraft("default"), EmptyPortalAgent.class, (world, type) -> new EmptyPortalAgent(type)));
    }
}
