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

import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockEntity;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class BlockEntityProtocol<T extends LanternBlockEntity> extends AbstractBlockEntityProtocol<T> {

    /**
     * Constructs a new {@link AbstractBlockEntityProtocol} object.
     *
     * @param blockEntity The block entity
     */
    protected BlockEntityProtocol(T blockEntity) {
        super(blockEntity);
    }

    /**
     * Gets the block entity type that should be used to
     * update the block entity on the client.
     *
     * @return The block entity type
     */
    protected abstract String getType();

    @Override
    protected void init(BlockEntityProtocolUpdateContext context) {
        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        populateInitData(dataView);
        context.send(new MessagePlayOutBlockEntity(getType(), this.blockEntity.getLocation().getBlockPosition(), dataView));
    }

    @Override
    protected void update(BlockEntityProtocolUpdateContext context) {
        final LazyDataView lazyDataView = new LazyDataView();
        populateUpdateData(lazyDataView);
        if (lazyDataView.dataView != null) {
            context.send(new MessagePlayOutBlockEntity(getType(), this.blockEntity.getLocation().getBlockPosition(), lazyDataView.dataView));
        }
    }

    static final class LazyDataView implements Supplier<DataView> {

        @Nullable DataView dataView;

        @Override
        public DataView get() {
            if (this.dataView == null) {
                this.dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
            }
            return this.dataView;
        }
    }

    @Override
    protected void destroy(BlockEntityProtocolUpdateContext context) {
    }

    /**
     * Populates the {@link DataView} to send to the client.
     *
     * @param dataView The data view
     */
    protected void populateInitData(DataView dataView) {
    }

    /**
     * Populates the {@link DataView} to send to the client.
     *
     * @param dataViewSupplier The data view supplier
     */
    protected void populateUpdateData(Supplier<DataView> dataViewSupplier) {
    }
}
