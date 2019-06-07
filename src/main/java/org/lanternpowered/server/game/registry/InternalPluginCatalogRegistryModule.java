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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kotlin.reflect.KClass;
import org.lanternpowered.server.catalog.InternalCatalogType;
import org.spongepowered.api.CatalogType;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class InternalPluginCatalogRegistryModule<T extends CatalogType> extends DefaultCatalogRegistryModule<T>
        implements InternalCatalogRegistryModule<T> {

    private final Int2ObjectMap<T> byInternalId = new Int2ObjectOpenHashMap<>();

    public InternalPluginCatalogRegistryModule() {
    }

    public InternalPluginCatalogRegistryModule(Class<?>... catalogClasses) {
        super(catalogClasses);
    }

    public InternalPluginCatalogRegistryModule(KClass<?>... catalogClasses) {
        super(catalogClasses);
    }

    public InternalPluginCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable String pattern) {
        super(catalogClasses, pattern);
    }

    protected boolean isDuplicateInternalIdAllowed() {
        return false;
    }

    @Override
    protected void doRegistration(T catalogType, boolean disallowInbuiltPluginIds) {
        final int internalId = ((InternalCatalogType) catalogType).getInternalId();
        checkArgument(isDuplicateInternalIdAllowed() || !this.byInternalId.containsKey(internalId),
                "The internal id %s is already in use", internalId);
        super.doRegistration(catalogType, disallowInbuiltPluginIds);
        this.byInternalId.putIfAbsent(internalId, catalogType);
    }

    @Override
    public Optional<T> getByInternalId(int internalId) {
        return Optional.ofNullable(this.byInternalId.get(internalId));
    }
}
