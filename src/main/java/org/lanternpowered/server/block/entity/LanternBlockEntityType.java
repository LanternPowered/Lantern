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
package org.lanternpowered.server.block.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.util.ToStringHelper;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.block.entity.BlockEntityType;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unchecked")
public final class LanternBlockEntityType extends DefaultCatalogType implements BlockEntityType {

    public static <T extends BlockEntity> LanternBlockEntityType of(CatalogKey key,
            Class<T> tileEntityClass, Supplier<T> tileEntitySupplier) {
        return new LanternBlockEntityType(key, tileEntityClass, (Supplier) tileEntitySupplier);
    }

    public static <T extends BlockEntity> LanternBlockEntityType of(CatalogKey key,
            Supplier<T> tileEntitySupplier) {
        return new LanternBlockEntityType(key, tileEntitySupplier.get().getClass(), (Supplier) tileEntitySupplier);
    }

    private final Class<? extends BlockEntity> tileEntityClass;
    private final Supplier<BlockEntity> tileEntityConstructor;

    @Nullable BlockState defaultBlock;

    private LanternBlockEntityType(CatalogKey key, Class<? extends BlockEntity> tileEntityClass,
            Supplier<BlockEntity> tileEntityConstructor) {
        super(key);
        this.tileEntityClass = checkNotNull(tileEntityClass, "tileEntityClass");
        this.tileEntityConstructor = tileEntityConstructor;
    }

    @Override
    public Class<? extends BlockEntity> getBlockEntityType() {
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
     * be used for this {@link BlockEntityType}.
     *
     * @param defaultBlock The default block state
     */
    public void setDefaultBlock(BlockState defaultBlock) {
        checkNotNull(defaultBlock, "defaultBlock");
        this.defaultBlock = defaultBlock;
    }

    /**
     * Gets the default {@link BlockState} that should
     * be used for this {@link BlockEntityType}.
     *
     * @return The default block state
     */
    public BlockState getDefaultBlock() {
        return checkNotNull(this.defaultBlock, "The default block isn't available.");
    }

    /**
     * Constructs a new {@link BlockEntity} for this blockEntity entity type.
     *
     * @return The constructed blockEntity entity
     */
    public LanternBlockEntity construct() {
        final LanternBlockEntity tileEntity = (LanternBlockEntity) this.tileEntityConstructor.get();
        tileEntity.setBlockEntityType(this);
        return tileEntity;
    }

}
