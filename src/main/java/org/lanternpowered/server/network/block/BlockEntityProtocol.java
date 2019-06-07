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

import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;

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
