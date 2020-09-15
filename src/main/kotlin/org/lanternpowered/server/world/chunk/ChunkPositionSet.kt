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
package org.lanternpowered.server.world.chunk

import it.unimi.dsi.fastutil.longs.LongIterator
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import org.lanternpowered.api.world.chunk.ChunkPosition

/**
 * Represents an iterator of [ChunkPosition]s.
 */
interface ChunkPositionIterator : Iterator<ChunkPosition> {

    override fun next(): ChunkPosition = this.nextPosition()

    fun nextPosition(): ChunkPosition
}

/**
 * Represents a mutable iterator of [ChunkPosition]s.
 */
interface MutableChunkPositionIterator : ChunkPositionIterator, MutableIterator<ChunkPosition>

/**
 * Represents a collection of [ChunkPosition]s.
 */
interface ChunkPositionCollection : Collection<ChunkPosition> {

    /**
     * Checks whether any of the chunk positions in the given collection
     * are also present in this collection.
     */
    fun containsAny(collection: Collection<ChunkPosition>): Boolean

    override fun contains(element: ChunkPosition): Boolean
    override fun iterator(): ChunkPositionIterator
}

/**
 * Represents a collection of [ChunkPosition]s.
 */
interface MutableChunkPositionCollection : ChunkPositionCollection, MutableCollection<ChunkPosition> {

    override fun iterator(): MutableChunkPositionIterator
}

class MergedChunkPositionCollection(
        collections: Collection<ChunkPositionCollection>
) : ChunkPositionCollection {

    private val collections: Collection<ChunkPositionCollection> = collections.distinct()

    override fun containsAny(collection: Collection<ChunkPosition>): Boolean = collection.any { contains(it) }
    override fun contains(element: ChunkPosition): Boolean = this.collections.any { it.contains(element) }
    override fun containsAll(elements: Collection<ChunkPosition>): Boolean = elements.all { contains(it) }

    override fun iterator(): ChunkPositionIterator {
        val it = this.collections.flatten().distinct().iterator()
        return object : ChunkPositionIterator {
            override fun nextPosition(): ChunkPosition = it.next()
            override fun hasNext(): Boolean = it.hasNext()
        }
    }

    override val size: Int
        get() = this.collections.flatten().distinct().count()

    override fun isEmpty() = this.collections.any { it.isNotEmpty() }
}

/**
 * A collection of [ChunkPosition]s based of a
 * minimum and maximum chunk position.
 */
class MinMaxChunkPositionCollection(
        private val min: ChunkPosition,
        private val max: ChunkPosition
) : ChunkPositionCollection {

    override val size: Int = (this.min.x - this.max.x + 1) * (this.min.y - this.max.y + 1) * (this.min.z - this.max.z + 1)

    override fun containsAny(collection: Collection<ChunkPosition>): Boolean {
        return if (collection is MinMaxChunkPositionCollection) {
            val oMin = collection.min
            val oMax = collection.max
            this.max.x >= oMin.x && oMax.x >= this.min.x &&
                    this.max.y >= oMin.y && oMax.y >= this.min.y &&
                    this.max.z >= oMin.z && oMax.z >= this.min.z
        } else collection.any { contains(it) }
    }

    override fun contains(element: ChunkPosition): Boolean =
            element.x in this.min.x..this.max.x &&
                    element.y in this.min.y..this.max.y &&
                    element.z in this.min.z..this.max.z

    override fun containsAll(elements: Collection<ChunkPosition>): Boolean = elements.all { contains(it) }

    override fun isEmpty(): Boolean = false

    override fun iterator(): ChunkPositionIterator {
        val it = sequence {
            for (x in min.x..max.x) {
                for (y in min.y..max.y) {
                    for (z in min.z..max.z) {
                        yield(ChunkPosition(x, y, z))
                    }
                }
            }
        }.iterator()
        return object : ChunkPositionIterator {
            override fun nextPosition(): ChunkPosition = it.next()
            override fun hasNext(): Boolean = it.hasNext()
        }
    }
}

/**
 * Represents a set of [ChunkPosition]s.
 */
inline class ChunkPositionSet(val backing: LongSet = LongOpenHashSet()) : MutableSet<ChunkPosition>, MutableChunkPositionCollection {

    override val size: Int
        get() = this.backing.size

    override fun contains(element: ChunkPosition): Boolean =
            this.backing.contains(element.packed)

    override fun containsAll(elements: Collection<ChunkPosition>): Boolean =
            elements.all { contains(it) }

    override fun isEmpty(): Boolean = this.backing.isEmpty()

    override fun iterator(): MutableChunkPositionIterator =
            Iterator(this.backing.iterator())

    override fun containsAny(collection: Collection<ChunkPosition>): Boolean =
            collection.any { contains(it) }

    override fun add(element: ChunkPosition): Boolean =
            this.backing.add(element.packed)

    override fun addAll(elements: Collection<ChunkPosition>): Boolean {
        var modified = false
        for (element in elements) {
            if (add(element))
                modified = true
        }
        return modified
    }

    override fun clear() =
            this.backing.clear()

    override fun remove(element: ChunkPosition): Boolean =
            this.backing.remove(element.packed)

    override fun removeAll(elements: Collection<ChunkPosition>): Boolean {
        var modified = false
        for (element in elements) {
            if (remove(element))
                modified = true
        }
        return modified
    }

    override fun retainAll(elements: Collection<ChunkPosition>): Boolean {
        val it = iterator()
        var modified = false
        while (it.hasNext()) {
            if (!elements.contains(it.next())) {
                modified = true
                it.remove()
            }
        }
        return modified
    }

    /**
     * Represents an iterator of [ChunkPosition]s.
     */
    private class Iterator(val backing: LongIterator) : MutableChunkPositionIterator {
        override fun hasNext(): Boolean = this.backing.hasNext()
        override fun nextPosition(): ChunkPosition = ChunkPosition(this.backing.nextLong())
        override fun remove() = this.backing.remove()
    }
}

