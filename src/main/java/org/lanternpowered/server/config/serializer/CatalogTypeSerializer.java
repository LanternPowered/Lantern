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
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;

public final class CatalogTypeSerializer implements TypeSerializer<CatalogType> {

    @Override
    public CatalogType deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        return Sponge.getRegistry().getType(type.getRawType().asSubclass(CatalogType.class), ResourceKey.resolve(value.getString()))
                .orElseThrow(() -> new ObjectMappingException("The catalog type is missing: " + value.getString()));
    }

    @Override
    public void serialize(TypeToken<?> type, CatalogType obj, ConfigurationNode value) {
        value.setValue(obj.getKey().toString());
    }

}
