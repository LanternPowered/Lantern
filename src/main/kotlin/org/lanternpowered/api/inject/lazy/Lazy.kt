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
package org.lanternpowered.api.inject.lazy

import com.google.inject.ProvidedBy

/**
 * Represents a value that will be instantiated lazily.
 */
@FunctionalInterface
@ProvidedBy(LazyProvider::class)
interface Lazy<T> {

    val value: T

    // Some convenient methods to access the value
    operator fun invoke(): T = this.value
    fun get() = this.value

    companion object {

        /**
         * Constructs a new [Lazy] with the given initializer.
         */
        fun <T> of(initializer: () -> T) = object : Lazy<T> {
            override val value: T by lazy(initializer)
        }
    }
}
