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
package org.lanternpowered.server.network.block

import org.spongepowered.api.block.entity.BlockEntity

/**
 * Represents the protocol to init, update and destroy a
 * certain [BlockEntity] within a chunk.
 *
 * @property blockEntity The block entity
 * @property T The blockEntity entity type
 */
abstract class BlockEntityProtocol<T : BlockEntity> protected constructor(
        protected val blockEntity: T
) {
    /**
     * The amount of ticks between every update (in ticks).
     */
    var updateInterval = 4

    @JvmField
    var updateTickCounter = 0

    /**
     * Initializes the [BlockEntity] for the
     * given [BlockEntityProtocolUpdateContext].
     *
     * @param context The block entity update context
     */
    abstract fun init(context: BlockEntityProtocolUpdateContext)

    /**
     * Updates the [BlockEntity] for the
     * given [BlockEntityProtocolUpdateContext].
     *
     * @param context The block entity update context
     */
    abstract fun update(context: BlockEntityProtocolUpdateContext)

    /**
     * Destroys the [BlockEntity] for the
     * given [BlockEntityProtocolUpdateContext].
     *
     * @param context The block entity update context
     */
    abstract fun destroy(context: BlockEntityProtocolUpdateContext)
}
