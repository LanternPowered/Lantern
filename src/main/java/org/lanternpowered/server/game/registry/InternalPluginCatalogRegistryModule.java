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
