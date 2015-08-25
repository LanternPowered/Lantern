package org.lanternpowered.server.catalog;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameRegistry;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * A {@link TypeSerializer} implementation that allows {@link CatalogType}
 * values to be used in object-mapped classes.
 */
public class CatalogTypeTypeSerializer implements TypeSerializer<CatalogType> {

    private final GameRegistry gameRegistry;

    public CatalogTypeTypeSerializer(GameRegistry gameRegistry) {
        this.gameRegistry = gameRegistry;
    }

    @Override
    public CatalogType deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Optional<? extends CatalogType> ret = this.gameRegistry.getType(type.getRawType().asSubclass(CatalogType.class), value.getString());
        if (!ret.isPresent()) {
            throw new ObjectMappingException("Input '" + value.getValue() + "' was not a valid value for type " + type);
        }
        return ret.get();
    }

    @Override
    public void serialize(TypeToken<?> type, CatalogType obj, ConfigurationNode value) throws ObjectMappingException {
        value.setValue(obj.getId());
    }
}
