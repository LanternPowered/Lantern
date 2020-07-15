package org.lanternpowered.api.entity.player

import kotlin.contracts.contract

typealias Player = org.spongepowered.api.entity.living.player.server.ServerPlayer

/**
 * Gets the sponge player as a lantern player.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun org.spongepowered.api.entity.living.player.Player.fix(): Player {
    contract {
        returns() implies (this@fix is Player)
    }
    return this as Player
}
