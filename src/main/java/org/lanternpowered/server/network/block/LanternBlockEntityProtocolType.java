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
package org.lanternpowered.server.network.block;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.util.ToStringHelper;
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
