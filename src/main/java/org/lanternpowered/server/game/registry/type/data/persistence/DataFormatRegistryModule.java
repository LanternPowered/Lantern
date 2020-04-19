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
package org.lanternpowered.server.game.registry.type.data.persistence;

import org.lanternpowered.server.data.persistence.HoconDataFormat;
import org.lanternpowered.server.data.persistence.json.JsonDataFormat;
import org.lanternpowered.server.data.persistence.nbt.NbtDataFormat;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.persistence.DataFormat;
import org.spongepowered.api.data.persistence.DataFormats;

public final class DataFormatRegistryModule extends DefaultCatalogRegistryModule<DataFormat> {

    public DataFormatRegistryModule() {
        super(DataFormats.class);
    }

    @Override
    public void registerDefaults() {
        register(new HoconDataFormat(CatalogKey.sponge("hocon")));
        register(new JsonDataFormat(CatalogKey.sponge("json")));
        register(new NbtDataFormat(CatalogKey.minecraft("nbt")));
    }
}
