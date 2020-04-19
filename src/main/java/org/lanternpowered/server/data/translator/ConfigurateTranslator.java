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
package org.lanternpowered.server.data.translator;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.lanternpowered.server.data.persistence.AbstractDataTranslator;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfigurateTranslator extends AbstractDataTranslator<ConfigurationNode> {

    private static final ConfigurateTranslator INSTANCE = new ConfigurateTranslator();

    /**
     * Get the instance of this translator.
     *
     * @return The instance of this translator
     */
    public static ConfigurateTranslator instance() {
        return INSTANCE;
    }

    private ConfigurateTranslator() {
        super(CatalogKey.of("sponge", "configuration_node"), TypeToken.of(ConfigurationNode.class));
    }

    @Override
    public ConfigurationNode translate(DataView view) throws InvalidDataException {
        final ConfigurationNode node = SimpleConfigurationNode.root();
        node.setValue(view.getMap(DataQuery.of()).get());
        return node;
    }

    @Override
    public DataContainer translate(ConfigurationNode node) throws InvalidDataException {
        checkNotNull(node, "node");
        final DataContainer container = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        @Nullable final Object value = node.getValue();
        @Nullable final Object key = node.getKey();
        if (value != null) {
            if (key == null || value instanceof Map || value instanceof List) {
                if (value instanceof Map) {
                    //noinspection unchecked
                    for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
                        container.set(DataQuery.of('.', entry.getKey().toString()), entry.getValue());
                    }
                } else if (key != null) {
                    container.set(DataQuery.of(key.toString()), value);
                }
            } else {
                container.set(DataQuery.of('.', key.toString()), value);
            }
        }
        return container;
    }
}
