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
