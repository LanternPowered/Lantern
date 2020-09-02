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
package org.lanternpowered.server.audience

import org.lanternpowered.api.audience.Audience
import org.lanternpowered.api.audience.audienceOf
import org.lanternpowered.server.LanternGame
import org.spongepowered.api.adventure.Audiences

class LanternAudiencesFactory(private val game: LanternGame) : Audiences.Factory {

    private val onlinePlayers: Audience by lazy { audienceOf(this.game.server.unsafePlayers) }

    override fun onlinePlayers(): Audience = this.onlinePlayers

    override fun withPermission(permission: String): Audience {
        val players = this.game.server.unsafePlayers.asSequence()
                .filter { player -> player.hasPermission(permission) }
                .asIterable()
        return audienceOf(players)
    }
}
