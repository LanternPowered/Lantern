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
package org.lanternpowered.server.block.tile;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.util.ToStringHelper;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityType;

import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public final class LanternTileEntityType extends DefaultCatalogType implements TileEntityType {

    public static <T extends TileEntity> LanternTileEntityType of(CatalogKey key,
            Class<T> tileEntityClass, Supplier<T> tileEntitySupplier) {
        return new LanternTileEntityType(key, tileEntityClass, (Supplier) tileEntitySupplier);
    }

    public static <T extends TileEntity> LanternTileEntityType of(CatalogKey key,
            Supplier<T> tileEntitySupplier) {
        return new LanternTileEntityType(key, tileEntitySupplier.get().getClass(), (Supplier) tileEntitySupplier);
    }

    private final Class<? extends TileEntity> tileEntityClass;
    private final Supplier<TileEntity> tileEntityConstructor;

    @Nullable BlockState defaultBlock;

    private LanternTileEntityType(CatalogKey key, Class<? extends TileEntity> tileEntityClass,
            Supplier<TileEntity> tileEntityConstructor) {
        super(key);
        this.tileEntityClass = checkNotNull(tileEntityClass, "tileEntityClass");
        this.tileEntityConstructor = tileEntityConstructor;
    }

    @Override
    public Class<? extends TileEntity> getTileEntityType() {
        return this.tileEntityClass;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .omitNullValues()
                .add("tileEntityClass", this.tileEntityClass)
                .add("defaultBlock", this.defaultBlock);
    }

    /**
     * Sets the default {@link BlockState} that should
     * be used for this {@link TileEntityType}.
     *
     * @param defaultBlock The default block state
     */
    public void setDefaultBlock(BlockState defaultBlock) {
        checkNotNull(defaultBlock, "defaultBlock");
        this.defaultBlock = defaultBlock;
    }

    /**
     * Gets the default {@link BlockState} that should
     * be used for this {@link TileEntityType}.
     *
     * @return The default block state
     */
    public BlockState getDefaultBlock() {
        return checkNotNull(this.defaultBlock, "The default block isn't available.");
    }

    /**
     * Constructs a new {@link TileEntity} for this tile entity type.
     *
     * @return The constructed tile entity
     */
    public LanternTileEntity construct() {
        final LanternTileEntity tileEntity = (LanternTileEntity) this.tileEntityConstructor.get();
        tileEntity.setTileEntityType(this);
        return tileEntity;
    }

}
