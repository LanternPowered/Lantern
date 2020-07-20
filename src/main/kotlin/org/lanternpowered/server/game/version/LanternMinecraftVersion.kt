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
package org.lanternpowered.server.game.version

import org.lanternpowered.api.MinecraftVersion
import org.lanternpowered.server.network.protocol.Protocol

data class LanternMinecraftVersion(
        private val name: String,
        val protocol: Int,
        private val legacy: Boolean
) : MinecraftVersion {

    override fun getName(): String = this.name
    override fun isLegacy(): Boolean = this.legacy

    override fun compareTo(other: MinecraftVersion): Int =
            compareValuesBy(this, other, { it.isLegacy }, { (it as LanternMinecraftVersion).protocol })

    companion object {

        private const val UNKNOWN_NAME = "unknown"

        @JvmField
        val UNKNOWN = LanternMinecraftVersion(UNKNOWN_NAME, -1, false)

        @JvmField
        val UNKNOWN_LEGACY = LanternMinecraftVersion(UNKNOWN_NAME, -1, true)
    }
}
