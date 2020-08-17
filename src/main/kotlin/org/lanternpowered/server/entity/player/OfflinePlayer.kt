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
package org.lanternpowered.server.entity.player

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.locationOf
import org.spongepowered.api.data.Keys
import org.spongepowered.api.profile.GameProfile

class OfflinePlayer(profile: GameProfile) : AbstractPlayer(profile) {

    private var _worldKey: NamespacedKey? = null

    init {
        keyRegistry {
            registerProvider(Keys.ACTIVE_ITEM) {
                get { null }
            }
        }
    }

    override fun setLocation(location: Location): Boolean {
        this._worldKey = location.worldKey
        this.position = location.position
        return true
    }

    override fun getLocation(): Location = locationOf(this.worldKey, this.position)

    override val worldKey: NamespacedKey
        get() = this._worldKey ?: error("The world key is not yet set.")
}
