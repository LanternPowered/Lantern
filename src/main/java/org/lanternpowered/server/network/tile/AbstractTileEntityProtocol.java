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

import org.lanternpowered.server.block.tile.LanternBlockEntity;

/**
 * Represents the protocol to init, update and destroy a
 * certain {@link LanternBlockEntity} within a chunk.
 *
 * @param <T> The tile entity type
 */
public abstract class AbstractTileEntityProtocol<T extends LanternBlockEntity> {

    protected final T tile;

    /**
     * The amount of ticks between every update.
     */
    private int updateInterval = 4;

    int updateTickCounter = 0;

    /**
     * Constructs a new {@link AbstractTileEntityProtocol} object.
     *
     * @param tile The tile entity
     */
    protected AbstractTileEntityProtocol(T tile) {
        checkNotNull(tile, "tile");
        this.tile = tile;
    }

    /**
     * Sets the update internal of this entity protocol in ticks.
     *
     * @param updateInterval The update interval (in ticks)
     */
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Gets the update internal of this entity protocol in ticks.
     *
     * @return The update interval (in ticks)
     */
    public int getUpdateInterval() {
        return this.updateInterval;
    }

    /**
     * Initializes the {@link LanternBlockEntity} for the
     * given {@link TileEntityProtocolUpdateContext}.
     *
     * @param context The tile entity update context
     */
    protected abstract void init(TileEntityProtocolUpdateContext context);

    /**
     * Updates the {@link LanternBlockEntity} for the
     * given {@link TileEntityProtocolUpdateContext}.
     *
     * @param context The tile entity update context
     */
    protected abstract void update(TileEntityProtocolUpdateContext context);

    /**
     * Destroys the {@link LanternBlockEntity} for the
     * given {@link TileEntityProtocolUpdateContext}.
     *
     * @param context The tile entity update context
     */
    protected abstract void destroy(TileEntityProtocolUpdateContext context);

}
