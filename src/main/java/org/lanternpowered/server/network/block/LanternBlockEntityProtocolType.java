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
package org.lanternpowered.server.network.block;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.util.ToStringHelper;
import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.spongepowered.api.CatalogKey;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class LanternBlockEntityProtocolType<T extends LanternBlockEntity> extends DefaultCatalogType implements BlockEntityProtocolType<T> {

    public static <T extends LanternBlockEntity> BlockEntityProtocolType<T> of(CatalogKey key,
            Class<T> tileEntityType, Function<T, ? extends AbstractBlockEntityProtocol<T>> entityProtocolSupplier) {
        checkNotNull(tileEntityType, "blockEntityType");
        checkNotNull(entityProtocolSupplier, "entityProtocolSupplier");
        return new LanternBlockEntityProtocolType<>(key, tileEntityType, entityProtocolSupplier);
    }

    public static <T extends LanternBlockEntity> BlockEntityProtocolType<T> of(CatalogKey key,
            Function<T, ? extends AbstractBlockEntityProtocol<T>> entityProtocolSupplier) {
        return of(key, (Class<T>) LanternBlockEntity.class, entityProtocolSupplier);
    }

    private final Class<T> blockEntityType;
    private final Function<T, AbstractBlockEntityProtocol<T>> protocolSupplier;

    private LanternBlockEntityProtocolType(CatalogKey key, Class<T> blockEntityType,
            Function<T, ? extends AbstractBlockEntityProtocol<T>> protocolSupplier) {
        super(key);
        this.protocolSupplier = (Function<T, AbstractBlockEntityProtocol<T>>) protocolSupplier;
        this.blockEntityType = blockEntityType;
    }

    @Override
    public Class<T> getBlockEntityType() {
        return this.blockEntityType;
    }

    @Override
    public Function<T, AbstractBlockEntityProtocol<T>> getSupplier() {
        return this.protocolSupplier;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper().add("blockEntityType", this.blockEntityType);
    }
}
