package org.lanternpowered.server.data.translator;

import static com.google.common.base.Preconditions.checkNotNull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.translator.DataTranslator;

import java.util.Map;

/**
 * A translator for translating {@link DataView}s into {@link ConfigurationNode}s.
 * 
 * TODO: A temporarily replacement for the sponge one until the bugs are fixed.
 */
public final class ConfigurateTranslator implements DataTranslator<ConfigurationNode> {

    private static final ConfigurateTranslator instance = new ConfigurateTranslator();

    private ConfigurateTranslator() {
    }

    /**
     * Get the instance of this translator.
     *
     * @return The instance of this translator
     */
    public static ConfigurateTranslator instance() {
        return instance;
    }

    private static void populateNode(ConfigurationNode node, DataView container) {
        checkNotNull(node, "node");
        checkNotNull(container, "container");
        Map<DataQuery, Object> values = container.getValues(false);
        for (Map.Entry<DataQuery, Object> entry : values.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof DataView || value instanceof DataSerializable) {
                if (value instanceof DataSerializable) {
                    container = ((DataSerializable) value).toContainer();
                } else {
                    container = (DataView) value;
                }
                populateNode(node.getNode(entry.getKey().getParts().get(0)), container);
            } else {
                node.getNode(entry.getKey().getParts().get(0)).setValue(value);
            }
        }
    }

    private static DataView translateFromNode(ConfigurationNode node) {
        checkNotNull(node, "node");
        DataContainer dataContainer = new MemoryDataContainer();
        if (node.getValue() != null) {
            if (node.getKey() == null) {
                translateMapOrList(node, dataContainer);
            } else {
                dataContainer.set(DataQuery.of('.', node.getKey().toString()), node.getValue());
            }
        }
        return dataContainer;
    }

    @SuppressWarnings("unchecked")
    private static void translateMapOrList(ConfigurationNode node, DataView container) {
        Object value = node.getValue();
        if (value instanceof Map) {
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
                container.set(DataQuery.of('.', entry.getKey().toString()), entry.getValue());
            }
        } else if (value != null) {
            container.set(DataQuery.of(node.getKey().toString()), value);
        }
    }

    @Override
    public ConfigurationNode translateData(DataView container) {
        ConfigurationNode node = SimpleConfigurationNode.root();
        translateContainerToData(node, container);
        return node;
    }

    @Override
    public void translateContainerToData(ConfigurationNode node, DataView container) {
        ConfigurateTranslator.populateNode(node, container);
    }

    @Override
    public DataView translateFrom(ConfigurationNode node) {
        return ConfigurateTranslator.translateFromNode(node);
    }

}