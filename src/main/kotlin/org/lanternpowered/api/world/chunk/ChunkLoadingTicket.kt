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
package org.lanternpowered.api.world.chunk

import org.lanternpowered.api.world.World

/**
 * Represents a ticket that can be used to keep [Chunk]s loaded in a world.
 */
interface ChunkLoadingTicket {

    /**
     * The world this loading ticket is applicable to.
     */
    val world: World

    /**
     * Whether this ticket is empty, so not holding any chunk references.
     */
    val isEmpty: Boolean

    /**
     * A set with all the chunks that are held by this loading ticket.
     */
    val chunks: Set<ChunkPosition>

    /**
     * Acquires a load reference on the given chunk position. As long as this
     * ticket is active (or any other ticket) that holds the chunk, it will
     * not be unloaded. If the chunk is currently unloaded, it will be queued
     * to be loaded.
     *
     * @param position The position of the chunk to force-load
     * @return Whether the chunk was added
     */
    fun acquire(position: ChunkPosition): Boolean

    /**
     * Releases the chunk from the reference set of this ticket.
     *
     * @param position The position of the chunk to remove from force-loading
     * @return Whether the chunk was removed
     */
    fun release(position: ChunkPosition): Boolean

    /**
     * Releases the chunk from the reference set of this ticket.
     *
     * @param positions The positions of the chunk to remove from force-loading
     * @return Whether the chunk was removed
     */
    fun releaseAll(positions: Iterable<ChunkPosition>): Boolean

    /**
     * Releases all the chunks from the reference set of this ticket.
     */
    fun releaseAll()
}
