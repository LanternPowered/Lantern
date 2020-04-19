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

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class InetAddressTypeSerializer implements TypeSerializer<InetAddress> {

    @Override
    public InetAddress deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        try {
            return InetAddress.getByName(value.getString());
        } catch (UnknownHostException e) {
            throw new ObjectMappingException(e);
        }
    }

    @Override
    public void serialize(TypeToken<?> type, InetAddress obj, ConfigurationNode value) throws ObjectMappingException {
        value.setValue(obj.getHostAddress());
    }

}
