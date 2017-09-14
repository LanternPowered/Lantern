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
package org.lanternpowered.server.game.registry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Maps;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.Tuple;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class PluginCatalogRegistryModule<T extends CatalogType> extends AbstractCatalogRegistryModule<T>
        implements CatalogMappingDataHolder {

    private final static String ID_PATTERN_VALUE = "^[a-z][a-z0-9-_]+:[a-z][a-z0-9-_]+$";
    private final static Pattern ID_PATTERN = Pattern.compile(ID_PATTERN_VALUE);

    @Nullable private Map<String, T> typesByName = null;

    public PluginCatalogRegistryModule(Class<?>... catalogClasses) {
        super(catalogClasses);
    }

    public PluginCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable String pattern) {
        super(catalogClasses, pattern);
    }

    public PluginCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable Function<T, String> mappingProvider) {
        super(catalogClasses, mappingProvider);
    }

    public PluginCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable Function<T, String> mappingProvider, @Nullable String pattern) {
        super(catalogClasses, mappingProvider, pattern);
    }

    @Override
    protected void register(T catalogType) {
        register(catalogType, false);
    }

    protected void register(T catalogType, boolean disallowInbuiltPluginIds) {
        validateCatalogType(catalogType, ID_PATTERN_VALUE, ID_PATTERN);
        final String id = catalogType.getId();
        final int index = id.indexOf(':');
        final String pluginId = id.substring(0, index);
        if (disallowInbuiltPluginIds) {
            for (String pluginId1 : InternalPluginsInfo.IDENTIFIERS) {
                checkArgument(!pluginId.equals(pluginId1), "Plugin trying to register a fake %s catalog type!", pluginId1);
            }
        }
        this.types.put(id, catalogType);
        final String name = catalogType.getName().toLowerCase(Locale.ENGLISH);
        if (this.typesByName != null && this.typesByName.containsKey(name)) {
            return;
        }
        final String id0 = id.substring(index + 1);
        if (id0.equals(name)) {
            for (String pluginId1 : InternalPluginsInfo.IDENTIFIERS) {
                if (this.types.containsKey(pluginId1 + ':' + id0)) {
                    return;
                }
            }
        }
        if (this.typesByName == null) {
            this.typesByName = new HashMap<>();
        }
        this.typesByName.put(name, catalogType);
    }

    @Override
    public Map<String, T> provideCatalogMap() {
        final Map<String, Tuple<T, Integer>> mappings = new HashMap<>();
        for (T type : this.types.values()) {
            final String mapping;

            final String id = type.getId().toLowerCase(Locale.ENGLISH);
            final int index = id.indexOf(':');
            final String pluginId = id.substring(0, index);

            if (this.mappingProvider != null) {
                final String mapping1 = this.mappingProvider.apply(type);
                if (mapping1 == null) {
                    continue;
                }
                mapping = mapping1.toLowerCase(Locale.ENGLISH);
            } else {
                mapping = id.substring(index + 1);
            }

            final int priority = InternalPluginsInfo.IDENTIFIERS.indexOf(pluginId);
            if (mappings.containsKey(mapping)) {
                if (priority == -1) {
                    continue;
                }
                final int lastPriority = mappings.get(mapping).getSecond();
                if (lastPriority != -1 && priority > lastPriority) {
                    continue;
                }
            }
            mappings.put(mapping, new Tuple<>(type, priority));
        }
        //noinspection ConstantConditions
        return Maps.transformEntries(mappings, (key, value) -> value.getFirst());
    }

    @Override
    public Optional<T> getById(String id) {
        id = checkNotNull(id, "id").toLowerCase(Locale.ENGLISH);
        if (id.indexOf(':') == -1) {
            for (String pluginId : InternalPluginsInfo.IDENTIFIERS) {
                final T type = this.types.get(pluginId + ':' + id);
                if (type != null) {
                    return Optional.of(type);
                }
            }
            if (this.typesByName != null) {
                return Optional.ofNullable(this.typesByName.get(id));
            }
            return Optional.empty();
        }
        final T type = this.types.get(id);
        if (type != null) {
            return Optional.of(type);
        }
        if (this.typesByName != null) {
            return Optional.ofNullable(this.typesByName.get(id));
        }
        return Optional.empty();
    }
}
