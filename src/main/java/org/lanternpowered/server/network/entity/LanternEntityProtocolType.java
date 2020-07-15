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
package org.lanternpowered.server.network.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.util.ToStringHelper;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.entity.LanternEntity;
import org.spongepowered.api.ResourceKey;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class LanternEntityProtocolType<E extends LanternEntity> extends DefaultCatalogType implements EntityProtocolType<E> {

    public static <E extends LanternEntity> EntityProtocolType<E> of(ResourceKey key,
            Class<E> entityType, Function<E, ? extends AbstractEntityProtocol<E>> entityProtocolSupplier) {
        checkNotNull(key, "key");
        checkNotNull(entityProtocolSupplier, "entityProtocolSupplier");
        return new LanternEntityProtocolType<>(key, entityType, entityProtocolSupplier);
    }

    public static <E extends LanternEntity> EntityProtocolType<E> of(ResourceKey key,
            Function<E, ? extends AbstractEntityProtocol<E>> entityProtocolSupplier) {
        return of(key, (Class<E>) LanternEntity.class, entityProtocolSupplier);
    }

    private final Class<E> entityType;
    private final Function<E, AbstractEntityProtocol<E>> entityProtocolSupplier;

    private LanternEntityProtocolType(ResourceKey key, Class<E> entityType,
            Function<E, ? extends AbstractEntityProtocol<E>> entityProtocolSupplier) {
        super(key);
        this.entityProtocolSupplier = (Function<E, AbstractEntityProtocol<E>>) entityProtocolSupplier;
        this.entityType = entityType;
    }

    @Override
    public Class<E> getEntityType() {
        return this.entityType;
    }

    @Override
    public Function<E, AbstractEntityProtocol<E>> getSupplier() {
        return this.entityProtocolSupplier;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper().add("entityType", this.entityType);
    }
}
