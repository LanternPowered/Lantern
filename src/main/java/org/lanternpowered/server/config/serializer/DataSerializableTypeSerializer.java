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
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.lanternpowered.server.data.translator.ConfigurateTranslator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataContainer;

import java.util.Optional;

/**
 * An implementation of {@link TypeSerializer} so that DataSerializables can be
 * provided in {@link ObjectMapper}-using classes.
 */
public final class DataSerializableTypeSerializer implements TypeSerializer<DataSerializable> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public DataSerializable deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        final DataManager dataManager = Sponge.getDataManager();
        final Optional<DataBuilder<?>> builderOpt = (Optional) dataManager.getBuilder(type.getRawType().asSubclass(DataSerializable.class));
        if (!builderOpt.isPresent()) {
            throw new ObjectMappingException("No data builder is registered for " + type);
        }
        final Optional<? extends DataSerializable> built = builderOpt.get().build(ConfigurateTranslator.instance().translate(value));
        if (!built.isPresent()) {
            throw new ObjectMappingException("Unable to build instance of " + type);
        }
        return built.get();
    }

    @Override
    public void serialize(TypeToken<?> type, DataSerializable obj, ConfigurationNode value) throws ObjectMappingException {
        final DataContainer container = obj.toContainer();
        value.setValue(ConfigurateTranslator.instance().translate(container));
    }

}
