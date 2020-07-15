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

import org.lanternpowered.server.data.type.LanternToolType;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.type.ToolType;
import org.spongepowered.api.data.type.ToolTypes;

public final class ToolTypeRegistryModule extends DefaultCatalogRegistryModule<ToolType> {

    public ToolTypeRegistryModule() {
        super(ToolTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternToolType(ResourceKey.minecraft("diamond")));
        register(new LanternToolType(ResourceKey.minecraft("gold")));
        register(new LanternToolType(ResourceKey.minecraft("iron")));
        register(new LanternToolType(ResourceKey.minecraft("stone")));
        register(new LanternToolType(ResourceKey.minecraft("wood")));
    }
}
