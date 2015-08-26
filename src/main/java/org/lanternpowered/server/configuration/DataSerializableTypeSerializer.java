package org.lanternpowered.server.configuration;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.service.persistence.DataBuilder;
import org.spongepowered.api.service.persistence.SerializationService;

/**
 * An implementation of {@link TypeSerializer} so that DataSerializables can be
 * provided in {@link ObjectMapper}-using classes.
 */
public class DataSerializableTypeSerializer implements TypeSerializer<DataSerializable> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public DataSerializable deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Optional<SerializationService> serviceOpt = LanternGame.get().getServiceManager().provide(SerializationService.class);
        if (!serviceOpt.isPresent()) {
            throw new ObjectMappingException("No serialization service is present!");
        }
        Optional<DataBuilder<?>> builderOpt = (Optional) serviceOpt.get().getBuilder(type.getRawType().asSubclass(DataSerializable.class));
        if (!builderOpt.isPresent()) {
            throw new ObjectMappingException("No data builder is registered for " + type);
        }
        Optional<? extends DataSerializable> built = builderOpt.get().build(ConfigurateTranslator.instance().translateFrom(value));
        if (!built.isPresent()) {
            throw new ObjectMappingException("Unable to build instance of " + type);
        }
        return built.get();
    }

    @Override
    public void serialize(TypeToken<?> type, DataSerializable obj, ConfigurationNode value) throws ObjectMappingException {
        DataContainer container = obj.toContainer();
        value.setValue(ConfigurateTranslator.instance().translateData(container));
    }
}
