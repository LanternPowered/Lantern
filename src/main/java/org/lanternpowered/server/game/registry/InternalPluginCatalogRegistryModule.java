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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.catalog.InternalCatalogType;
import org.lanternpowered.server.catalog.VirtualCatalogType;
import org.spongepowered.api.CatalogType;

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

public class InternalPluginCatalogRegistryModule<T extends CatalogType> extends PluginCatalogRegistryModule<T>
        implements InternalCatalogRegistryModule<T> {

    private final Int2ObjectMap<T> byInternalId = new Int2ObjectOpenHashMap<>();

    public InternalPluginCatalogRegistryModule(Class<?>... catalogClasses) {
        super(catalogClasses);
    }

    public InternalPluginCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable String pattern) {
        super(catalogClasses, pattern);
    }

    public InternalPluginCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable Function<T, String> mappingProvider) {
        super(catalogClasses, mappingProvider);
    }

    public InternalPluginCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable Function<T, String> mappingProvider, @Nullable String pattern) {
        super(catalogClasses, mappingProvider, pattern);
    }

    protected boolean isDuplicateInternalIdAllowed() {
        return false;
    }

    @Override
    protected void register(T catalogType, boolean disallowInbuiltPluginIds) {
        checkNotNull(catalogType, "catalogType");
        final int internalId = ((InternalCatalogType) catalogType).getInternalId();
        checkArgument(this.isDuplicateInternalIdAllowed() || !this.byInternalId.containsKey(internalId),
                "The internal id %s is already in use", internalId);
        super.register(catalogType, disallowInbuiltPluginIds);
        this.byInternalId.putIfAbsent(internalId, catalogType);
    }

    @Override
    public Optional<T> getByInternalId(int internalId) {
        return Optional.ofNullable(this.byInternalId.get(internalId));
    }

    protected Int2ObjectMap<T> getInternalIdMappings() {
        return this.byInternalId;
    }

    protected Object2IntMap<String> getRegistryDataMappings() {
        final Object2IntMap<String> mappings = new Object2IntOpenHashMap<>();
        this.byInternalId.int2ObjectEntrySet().forEach(entry -> {
            if (!(entry.getValue() instanceof VirtualCatalogType)) {
                mappings.put(entry.getValue().getId(), entry.getIntKey());
            }
        });
        return mappings;
    }
}
