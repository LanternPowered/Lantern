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
package org.lanternpowered.server.script;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.script.ObjectType;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractObjectTypeRegistryModule<O, T extends ObjectType<O>> extends DefaultCatalogRegistryModule<T> {

    private final Map<Class<?>, T> byClass = new HashMap<>();

    public AbstractObjectTypeRegistryModule(Class<?>... catalogClasses) {
        super(catalogClasses);
    }

    @Override
    protected void doRegistration(T catalogType, boolean disallowInbuiltPluginIds) {
        super.doRegistration(catalogType, disallowInbuiltPluginIds);
        this.byClass.put(catalogType.getType(), catalogType);
    }

    /**
     * Gets the {@link T} for the specified type class of the type {@link O}.
     *
     * @param clazz The value class
     * @return The object type
     */
    public Optional<T> getByClass(Class<? extends O> clazz) {
        return Optional.ofNullable(this.byClass.get(checkNotNull(clazz, "clazz")));
    }
}
