/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
        return null;
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
