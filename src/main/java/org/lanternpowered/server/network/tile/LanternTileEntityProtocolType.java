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
package org.lanternpowered.server.network.tile;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.util.ToStringHelper;
import org.spongepowered.api.CatalogKey;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class LanternTileEntityProtocolType<T extends LanternTileEntity> extends DefaultCatalogType implements TileEntityProtocolType<T> {

    public static <T extends LanternTileEntity> TileEntityProtocolType<T> of(CatalogKey key,
            Class<T> tileEntityType, Function<T, ? extends AbstractTileEntityProtocol<T>> entityProtocolSupplier) {
        checkNotNull(tileEntityType, "tileEntityType");
        checkNotNull(entityProtocolSupplier, "entityProtocolSupplier");
        return new LanternTileEntityProtocolType<>(key, tileEntityType, entityProtocolSupplier);
    }

    public static <T extends LanternTileEntity> TileEntityProtocolType<T> of(CatalogKey key,
            Function<T, ? extends AbstractTileEntityProtocol<T>> entityProtocolSupplier) {
        return of(key, (Class<T>) LanternTileEntity.class, entityProtocolSupplier);
    }

    private final Class<T> tileEntityType;
    private final Function<T, AbstractTileEntityProtocol<T>> protocolSupplier;

    private LanternTileEntityProtocolType(CatalogKey key, Class<T> tileEntityType,
            Function<T, ? extends AbstractTileEntityProtocol<T>> protocolSupplier) {
        super(key);
        this.protocolSupplier = (Function<T, AbstractTileEntityProtocol<T>>) protocolSupplier;
        this.tileEntityType = tileEntityType;
    }

    @Override
    public Class<T> getTileEntityType() {
        return this.tileEntityType;
    }

    @Override
    public Function<T, AbstractTileEntityProtocol<T>> getSupplier() {
        return this.protocolSupplier;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper().add("tileEntityType", this.tileEntityType);
    }
}
