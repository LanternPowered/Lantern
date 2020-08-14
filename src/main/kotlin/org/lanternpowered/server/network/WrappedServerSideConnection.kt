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

import org.spongepowered.api.network.PlayerConnection
import org.spongepowered.api.network.ServerSideConnection

/**
 * A [ServerSideConnection] that wraps around another [ServerSideConnection]. Mainly
 * used to hide the [PlayerConnection] when passing in a [NetworkSession]
 * in events where the player object is still unavailable.
 */
class WrappedServerSideConnection(private val connection: ServerSideConnection) : ServerSideConnection by connection
