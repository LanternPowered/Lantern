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

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.CatalogType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class SimpleCatalogRegistryModule<T extends CatalogType> extends AbstractCatalogRegistryModule<T> {

    private final static String ID_PATTERN_VALUE = "^[a-z][a-z0-9-_]+$";
    private final static Pattern ID_PATTERN = Pattern.compile(ID_PATTERN_VALUE);

    @Nullable private Map<String, T> typesByName = null;

    public SimpleCatalogRegistryModule(Class<?>... catalogClasses) {
        super(catalogClasses);
    }

    public SimpleCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable Function<T, String> mappingProvider) {
        super(catalogClasses, mappingProvider);
    }

    @Override
    protected void finalizeContent() {
        checkFinalizedContent();
        // Merge the content to reduce hash lookups
        if (this.typesByName != null) {
            for (Map.Entry<String, T> entry : this.typesByName.entrySet()) {
                this.types.putIfAbsent(entry.getKey(), entry.getValue());
            }
            this.typesByName = null;
        }
        super.finalizeContent();
    }

    @Override
    protected void register(T catalogType) {
        validateCatalogType(catalogType, ID_PATTERN_VALUE, ID_PATTERN);
        final String id = catalogType.getId();
        this.types.put(id, catalogType);
        final String name = catalogType.getName().toLowerCase(Locale.ENGLISH);
        if (!id.equals(name)) {
            if (this.typesByName == null) {
                this.typesByName = new HashMap<>();
            }
            this.typesByName.putIfAbsent(name, catalogType);
        }
    }

    @Override
    public Map<String, T> provideCatalogMap() {
        final Map<String, T> mappings = new HashMap<>();
        for (T type : this.types.values()) {
            final String mapping;
            if (this.mappingProvider != null) {
                final String mapping1 = this.mappingProvider.apply(type);
                if (mapping1 == null) {
                    continue;
                }
                mapping = mapping1.toLowerCase(Locale.ENGLISH);
            } else {
                mapping = type.getId().toLowerCase(Locale.ENGLISH);
            }
            mappings.putIfAbsent(mapping, type);
        }
        return mappings;
    }

    @Override
    public Optional<T> getById(String id) {
        id = checkNotNull(id, "id").toLowerCase(Locale.ENGLISH);
        final T type = this.types.get(checkNotNull(id, "id").toLowerCase(Locale.ENGLISH));
        if (type != null) {
            return Optional.of(type);
        }
        return this.typesByName == null ? Optional.empty() : Optional.ofNullable(this.typesByName.get(id));
    }
}
