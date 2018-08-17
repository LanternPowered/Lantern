package org.lanternpowered.api.shard

import org.lanternpowered.api.ext.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Represents the type of a [Shard].
 */
class ShardType<T : Shard> internal constructor(val baseType: Class<T>) {

    companion object {

        private val cache = ConcurrentHashMap<Class<*>, ShardType<*>>()

        /**
         * Finds the [ShardType] for the given [Shard] class,
         * this checks super classes until a direct subclass
         * of [Shard] is found.
         */
        fun <T : Shard> find(type: Class<T>): ShardType<in T> {
            if (type === Shard::class.java) {
                throw IllegalArgumentException("Shard is never a valid shard type.")
            }
            var type1 = type
            while (type1.superclass !== Shard::class.java) {
                type1 = type1.superclass.uncheckedCast()
            }
            return this.cache.computeIfAbsent(type1) { ShardType<T>(it.uncheckedCast()) }.uncheckedCast()
        }

        /**
         * Finds the [ShardType] for the given [Shard] class,
         * this strictly checks whether the given class is a
         * direct subclass of [Shard], otherwise a error will
         * be thrown.
         */
        fun <T : Shard> findStrict(type: Class<T>): ShardType<T> {
            if (type === Shard::class.java) {
                throw IllegalArgumentException("Shard is never a valid shard type.")
            }
            if (type.superclass !== Shard::class.java) {
                throw IllegalArgumentException("${type.name} must be a direct subclass of Shard.")
            }
            return this.cache.computeIfAbsent(type) { ShardType<T>(it.uncheckedCast()) }.uncheckedCast()
        }
    }
}
