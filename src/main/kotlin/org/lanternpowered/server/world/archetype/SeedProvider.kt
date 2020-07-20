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
package org.lanternpowered.server.world.archetype

import java.util.concurrent.ThreadLocalRandom

sealed class SeedProvider {

    abstract fun get(): Long

    class Constant(private val seed: Long) : SeedProvider() {
        override fun get(): Long = this.seed
    }

    object Random : SeedProvider() {
        override fun get(): Long = ThreadLocalRandom.current().nextLong()
    }
}
