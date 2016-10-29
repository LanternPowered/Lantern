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
import org.lanternpowered.server.catalog.InternalCatalogType;
import org.spongepowered.api.CatalogType;

import java.util.Optional;

import javax.annotation.Nullable;

public class InternalEnumValueRegistryModule<V extends CatalogType> extends EnumValueRegistryModule<V>
        implements InternalCatalogRegistryModule<V> {

    private final Int2ObjectMap<V> byInternalId = new Int2ObjectOpenHashMap<>();

    public InternalEnumValueRegistryModule(Class<? extends Enum<?>> enumType, @Nullable Class<?> catalogClass) {
        super(enumType, catalogClass);
    }

    @Override
    public void registerDefaults() {
        super.registerDefaults();
        for (Enum<?> type : this.enumType.getEnumConstants()) {
            //noinspection unchecked
            final V catalogType = (V) type;
            final int internalId = ((InternalCatalogType) catalogType).getInternalId();
            checkArgument(!this.byInternalId.containsKey(internalId),
                    "The internal id %s is already in use", internalId);
            this.byInternalId.put(internalId, catalogType);
        }
    }

    @Override
    public Optional<V> getByInternalId(int internalId) {
        return Optional.ofNullable(this.byInternalId.get(internalId));
    }
}
