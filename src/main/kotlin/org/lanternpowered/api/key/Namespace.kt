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
package org.lanternpowered.api.key

/**
 * Represents a namespace.
 */
data class Namespace(val name: String) {

    /**
     * Gets a key within this [Namespace].
     */
    fun key(value: String): NamespacedKey = NamespacedKey.of(this.name, value)

    companion object {

        /**
         * The minecraft namespace.
         */
        val MINECRAFT = Namespace("minecraft")

        /**
         * The sponge namespace.
         */
        val SPONGE = Namespace("sponge")

        /**
         * The lantern namespace.
         */
        val LANTERN = Namespace("lantern")
    }
}
