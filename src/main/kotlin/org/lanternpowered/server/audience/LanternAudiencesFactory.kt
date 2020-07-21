package org.lanternpowered.server.audience

import org.lanternpowered.api.audience.Audience
import org.lanternpowered.server.LanternGame
import org.spongepowered.api.adventure.Audiences

class LanternAudiencesFactory(private val game: LanternGame) : Audiences.Factory {

    private val onlinePlayers: Audience by lazy { Audience.of(this.game.server.unsafePlayers) }

    override fun onlinePlayers(): Audience = this.onlinePlayers
}
