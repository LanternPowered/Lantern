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
package org.lanternpowered.server.data.translator;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import org.lanternpowered.server.data.persistence.AbstractDataTranslator;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.List;
import java.util.Map;

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
        super("sponge", "configuration_node", TypeToken.of(ConfigurationNode.class));
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
        final Object value = node.getValue();
        final Object key = node.getKey();
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
