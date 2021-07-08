package org.lanternpowered.api.shard

import org.lanternpowered.api.ext.*
import kotlin.reflect.KClass

/**
 * Represents the [Shard] auto attach support.
 */
sealed class AutoAttach<T : Shard<*>> {

    /**
     * A custom [AutoAttach] implementation so that you
     * can provide a fallback [Shard] implementation.
     *
     * The [default] will still be used if the provided
     * custom one isn't applicable.
     *
     * @param type The type of the shard implementation
     */
    data class Custom<T : Shard<*>>(val type: Class<out T>) : AutoAttach<T>() {

        constructor(type: KClass<T>) : this(type.java)
    }

    companion object {

        private object Disabled : AutoAttach<Shard<*>>()
        private object Default : AutoAttach<Shard<*>>()

        /**
         * Provides the disabled state of the auto attach support.
         */
        fun <T : Shard<T>> disabled(): AutoAttach<T> = Disabled.uncheckedCast()

        /**
         * A default implementation of a [Shard] will be attempted
         * to be used when the [Shard] needs to be attached.
         *
         * Default implementations can be registered through the
         * [ShardRegistry].
         */
        fun <T : Shard<T>> default(): AutoAttach<T> = Default.uncheckedCast()
    }
}
