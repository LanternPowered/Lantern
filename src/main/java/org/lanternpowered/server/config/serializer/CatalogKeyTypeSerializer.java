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
package org.lanternpowered.server.config.serializer;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.CatalogKey;

public final class CatalogKeyTypeSerializer implements TypeSerializer<CatalogKey> {

    @Override
    public CatalogKey deserialize(TypeToken<?> type, ConfigurationNode value) {
        return CatalogKey.resolve(value.getString());
    }

    @Override
    public void serialize(TypeToken<?> type, CatalogKey obj, ConfigurationNode value) {
        value.setValue(obj.toString());
    }
}
