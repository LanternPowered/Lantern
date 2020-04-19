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

import org.lanternpowered.server.data.type.LanternRabbitType;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.RabbitType;
import org.spongepowered.api.data.type.RabbitTypes;

public class RabbitTypeRegistryModule extends InternalPluginCatalogRegistryModule<RabbitType> {

    public RabbitTypeRegistryModule() {
        super(RabbitTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternRabbitType(CatalogKey.minecraft("brown"), 0));
        register(new LanternRabbitType(CatalogKey.minecraft("white"), 1));
        register(new LanternRabbitType(CatalogKey.minecraft("black"), 2));
        register(new LanternRabbitType(CatalogKey.minecraft("black_and_white"), 3));
        register(new LanternRabbitType(CatalogKey.minecraft("gold"), 4));
        register(new LanternRabbitType(CatalogKey.minecraft("salt_and_pepper"), 5));
        register(new LanternRabbitType(CatalogKey.minecraft("killer"), 99));
    }
}
