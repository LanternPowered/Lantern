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
package org.lanternpowered.server.data.io.store.item;

import org.lanternpowered.server.catalog.InternalCatalogType;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.InternalCatalogRegistryModule;
import org.lanternpowered.server.util.functions.Int2IntFunction;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

import javax.annotation.Nullable;

public class DataValueItemTypeObjectSerializer<T extends CatalogType> extends ItemTypeObjectSerializer {

    private final Key<? extends BaseValue<T>> key;
    private final InternalCatalogRegistryModule<T> registryModule;

    @Nullable private final Int2IntFunction dataValueToInternalId;
    @Nullable private final Int2IntFunction internalIdToDataValue;

    public DataValueItemTypeObjectSerializer(Key<? extends BaseValue<T>> key, InternalCatalogRegistryModule<T> registryModule,
            Int2IntFunction dataValueToInternalId, Int2IntFunction internalIdToDataValue) {
        this(dataValueToInternalId, internalIdToDataValue, key, registryModule);
    }

    public DataValueItemTypeObjectSerializer(Key<? extends BaseValue<T>> key, InternalCatalogRegistryModule<T> registryModule) {
        this(null, null, key, registryModule);
    }

    private DataValueItemTypeObjectSerializer(@Nullable Int2IntFunction dataValueToInternalId, @Nullable Int2IntFunction internalIdToDataValue,
            Key<? extends BaseValue<T>> key, InternalCatalogRegistryModule<T> registryModule) {
        this.key = key;
        this.registryModule = registryModule;
        this.dataValueToInternalId = dataValueToInternalId;
        this.internalIdToDataValue = internalIdToDataValue;
    }

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(itemStack, valueContainer, dataView);
        final Optional<T> type = valueContainer.remove(this.key);
        if (type.isPresent()) {
            int internalId = ((InternalCatalogType) type.get()).getInternalId();
            if (this.internalIdToDataValue != null) {
                internalId = this.internalIdToDataValue.apply(internalId);
            }
            dataView.set(DATA_VALUE, internalId);
        }
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(itemStack, valueContainer, dataView);
        final Optional<Integer> dataValue = dataView.getInt(DATA_VALUE);
        if (dataValue.isPresent()) {
            int internalId = dataValue.get();
            if (this.dataValueToInternalId != null) {
                internalId = this.dataValueToInternalId.apply(internalId);
            }
            final Optional<T> catalogType = this.registryModule.getByInternalId(internalId);
            if (catalogType.isPresent()) {
                valueContainer.set(this.key, catalogType.get());
            } else {
                Lantern.getLogger().warn("Deserialize an unknown catalog type {} with internal id {}",
                        this.key.getElementToken(), internalId);
            }
        }
    }
}
