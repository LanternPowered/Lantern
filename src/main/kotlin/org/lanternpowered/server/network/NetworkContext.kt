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
package org.lanternpowered.server.network

import io.netty.channel.Channel
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.LanternServer

interface NetworkContext {

    /**
     * The [NetworkSession] that is targeted.
     */
    val session: NetworkSession

    /**
     * The [Channel] that is targeted.
     */
    val channel: Channel

    val server: LanternServer
        get() = this.session.server

    val game: LanternGame
        get() = this.session.server.game
}
