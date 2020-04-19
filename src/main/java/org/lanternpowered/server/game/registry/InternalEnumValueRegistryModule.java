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
package org.lanternpowered.server.game.registry;

import static com.google.common.base.Preconditions.checkArgument;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.server.catalog.InternalCatalogType;
import org.spongepowered.api.CatalogType;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

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
