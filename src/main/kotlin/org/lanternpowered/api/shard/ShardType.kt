package org.lanternpowered.api.shard

/**
 * Represents the type of a [Shard].
 */
interface ShardType<T : Shard<T>> {

    /**
     * The base [Shard] class which
     * represents this shard type.
     */
    val type: Class<T>
}
