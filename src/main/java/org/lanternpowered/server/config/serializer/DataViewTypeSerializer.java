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
import org.lanternpowered.server.data.translator.ConfigurateTranslator;
import org.spongepowered.api.data.persistence.DataView;

public final class DataViewTypeSerializer implements TypeSerializer<DataView> {

    @Override
    public DataView deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        return ConfigurateTranslator.instance().translate(value);
    }

    @Override
    public void serialize(TypeToken<?> type, DataView obj, ConfigurationNode value) throws ObjectMappingException {
        value.setValue(ConfigurateTranslator.instance().translate(obj));
    }
}
