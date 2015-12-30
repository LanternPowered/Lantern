/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.catalog;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.spongepowered.api.CatalogType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class LanternCatalogTypeRegistry<T extends CatalogType> implements CatalogTypeRegistry<T> {

    private final ConcurrentMap<String, T> types = Maps.newConcurrentMap();

    @Override
    public Set<T> getAll() {
        return ImmutableSet.copyOf(this.types.values());
    }

    @Override
    public void register(T catalogType) {
        String id = checkNotNull(catalogType, "catalogType").getId().toLowerCase();
        checkNotNullOrEmpty(id, "identifier");
        checkState(this.types.putIfAbsent(id, catalogType) == null, "identifier already present (" + id + ")");
    }

    @Override
    public Optional<T> get(String identifier) {
        return Optional.ofNullable(this.types.get(checkNotNullOrEmpty(identifier, "identifier").toLowerCase()));
    }

    @Override
    public <V extends T> Optional<V> getOf(String identifier, Class<V> type) {
        T catalogType = this.types.get(checkNotNullOrEmpty(identifier, "identifier").toLowerCase());
        if (type.isInstance(catalogType)) {
            return Optional.<V>of(type.cast(catalogType));
        }
        return Optional.empty();
    }

    @Override
    public <V extends T> Set<V> getAllOf(Class<V> type) {
        ImmutableSet.Builder<V> builder = ImmutableSet.builder();
        for (T catalogType : this.types.values()) {
            if (type.isInstance(catalogType)) {
                builder.add(type.cast(catalogType));
            }
        }
        return builder.build();
    }

    @Override
    public Map<String, T> getDelegateMap() {
        return this.types;
    }

    @Override
    public boolean has(T catalogType) {
        return this.types.containsValue(checkNotNull(catalogType, "catalogType"));
    }

    @Override
    public boolean has(String identifier) {
        return this.types.containsKey(checkNotNull(identifier, "identifier").toLowerCase());
    }
}
