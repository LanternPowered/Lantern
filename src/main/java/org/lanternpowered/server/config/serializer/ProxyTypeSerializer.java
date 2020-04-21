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
import org.lanternpowered.server.network.ProxyType;

public final class ProxyTypeSerializer implements TypeSerializer<ProxyType> {

    @Override
    public ProxyType deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        return ProxyType.getByName(value.getString()).orElseThrow(() -> new IllegalArgumentException("Invalid proxy type: " + value.getString()));
    }

    @Override
    public void serialize(TypeToken<?> type, ProxyType obj, ConfigurationNode value) throws ObjectMappingException {
        value.setValue(obj.getDisplayName());
    }
}
