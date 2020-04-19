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
