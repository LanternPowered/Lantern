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

import org.lanternpowered.server.data.type.LanternLlamaType;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.LlamaType;
import org.spongepowered.api.data.type.LlamaTypes;

public class LlamaTypeRegistryModule extends DefaultCatalogRegistryModule<LlamaType> {

    public LlamaTypeRegistryModule() {
        super(LlamaTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternLlamaType(CatalogKey.minecraft("creamy"), 0));
        register(new LanternLlamaType(CatalogKey.minecraft("white"), 1));
        register(new LanternLlamaType(CatalogKey.minecraft("brown"), 2));
        register(new LanternLlamaType(CatalogKey.minecraft("gray"), 3));
    }
}
