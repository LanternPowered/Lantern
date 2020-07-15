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
import org.spongepowered.api.ResourceKey;

public final class ResourceKeyTypeSerializer implements TypeSerializer<ResourceKey> {

    @Override
    public ResourceKey deserialize(TypeToken<?> type, ConfigurationNode value) {
        return ResourceKey.resolve(value.getString());
    }

    @Override
    public void serialize(TypeToken<?> type, ResourceKey obj, ConfigurationNode value) {
        value.setValue(obj.toString());
    }
}
