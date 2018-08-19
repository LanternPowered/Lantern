package org.lanternpowered.server.shard

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.shard.Shard
import org.lanternpowered.api.shard.ShardHolder
import org.lanternpowered.api.shard.ShardType

/**
 * Represents the requirements of a specific
 * [Shard] before it can be attached.
 */
class ShardRequirements(
        private val target: Class<out Shard<*>>,
        requiredHolderTypes: Collection<Class<*>>) {

    /**
     * The shard type of this requirements, if known.
     */
    var shardType: ShardType<*>? = null
        internal set

    /**
     * A list with all the holder classes that
     * should be implemented before the holder
     * is considered applicable.
     */
    private val requiredHolderTypes: Collection<Class<*>> = requiredHolderTypes.toImmutableList()

    /**
     * Tests whether the shard can be attached to the given [ShardHolder]
     * based on the [ShardHolder] requirements.
     */
    fun testHolder(holder: ShardHolder): Boolean {
        this.requiredHolderTypes.forEach {
            if (!it.isInstance(holder)) {
                return false
            }
        }
        return true
    }
}
