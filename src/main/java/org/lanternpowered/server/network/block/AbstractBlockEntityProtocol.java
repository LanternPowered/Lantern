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

/**
 * Represents the protocol to init, update and destroy a
 * certain {@link LanternBlockEntity} within a chunk.
 *
 * @param <T> The blockEntity entity type
 */
public abstract class AbstractBlockEntityProtocol<T extends LanternBlockEntity> {

    protected final T blockEntity;

    /**
     * The amount of ticks between every update.
     */
    private int updateInterval = 4;

    int updateTickCounter = 0;

    /**
     * Constructs a new {@link AbstractBlockEntityProtocol} object.
     *
     * @param blockEntity The block entity
     */
    protected AbstractBlockEntityProtocol(T blockEntity) {
        checkNotNull(blockEntity, "blockEntity");
        this.blockEntity = blockEntity;
    }

    /**
     * Sets the update internal of this block entity protocol in ticks.
     *
     * @param updateInterval The update interval (in ticks)
     */
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Gets the update internal of this block entity protocol in ticks.
     *
     * @return The update interval (in ticks)
     */
    public int getUpdateInterval() {
        return this.updateInterval;
    }

    /**
     * Initializes the {@link LanternBlockEntity} for the
     * given {@link BlockEntityProtocolUpdateContext}.
     *
     * @param context The block entity update context
     */
    protected abstract void init(BlockEntityProtocolUpdateContext context);

    /**
     * Updates the {@link LanternBlockEntity} for the
     * given {@link BlockEntityProtocolUpdateContext}.
     *
     * @param context The block entity update context
     */
    protected abstract void update(BlockEntityProtocolUpdateContext context);

    /**
     * Destroys the {@link LanternBlockEntity} for the
     * given {@link BlockEntityProtocolUpdateContext}.
     *
     * @param context The block entity update context
     */
    protected abstract void destroy(BlockEntityProtocolUpdateContext context);

}
