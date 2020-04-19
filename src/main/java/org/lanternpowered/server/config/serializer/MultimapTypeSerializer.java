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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.Map;

public final class MultimapTypeSerializer implements TypeSerializer<Multimap> {

    @Override
    public Multimap deserialize(TypeToken<?> type, ConfigurationNode node) throws ObjectMappingException {
        final Multimap<Object, Object> multimap = LinkedHashMultimap.create();
        if (node.hasMapChildren()) {
            TypeToken<?> key = type.resolveType(Multimap.class.getTypeParameters()[0]);
            TypeToken<?> value = type.resolveType(Multimap.class.getTypeParameters()[1]);
            TypeSerializer keySerial = node.getOptions().getSerializers().get(key);
            TypeSerializer valueSerial = node.getOptions().getSerializers().get(value);

            if (keySerial == null) {
                throw new ObjectMappingException("No type serializer available for type " + key);
            }

            if (valueSerial == null) {
                throw new ObjectMappingException("No type serializer available for type " + value);
            }

            for (Map.Entry<Object, ? extends ConfigurationNode> ent : node.getChildrenMap().entrySet()) {
                Object keyValue = keySerial.deserialize(key, SimpleConfigurationNode.root().setValue(ent.getKey()));
                Object valueValue = valueSerial.deserialize(value, ent.getValue());
                if (keyValue == null || valueValue == null) {
                    continue;
                }

                multimap.put(keyValue, valueValue);
            }
        }
        return multimap;
    }

    @Override
    public void serialize(TypeToken<?> type, Multimap obj, ConfigurationNode node) throws ObjectMappingException {
        TypeToken<?> key = type.resolveType(Multimap.class.getTypeParameters()[0]);
        TypeToken<?> value = type.resolveType(Multimap.class.getTypeParameters()[1]);
        TypeSerializer keySerial = node.getOptions().getSerializers().get(key);
        TypeSerializer valueSerial = node.getOptions().getSerializers().get(value);

        if (keySerial == null) {
            throw new ObjectMappingException("No type serializer available for type " + key);
        }

        if (valueSerial == null) {
            throw new ObjectMappingException("No type serializer available for type " + value);
        }

        node.setValue(ImmutableMap.of());
        for (Object k : obj.keySet()) {
            for (Object v : obj.get(k)) {
                SimpleConfigurationNode keyNode = SimpleConfigurationNode.root();
                keySerial.serialize(key, k, keyNode);
                valueSerial.serialize(value, v, node.getNode(keyNode.getValue()));
            }
        }
    }
}
