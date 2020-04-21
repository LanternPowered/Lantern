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

import org.spongepowered.api.network.RemoteConnection
import org.spongepowered.api.network.PlayerConnection

/**
 * A [RemoteConnection] that wraps around another [RemoteConnection]. Mainly
 * used to hide the [PlayerConnection] when passing in a [NetworkSession]
 * in status events.
 */
class WrappedRemoteConnection(remoteConnection: RemoteConnection) : RemoteConnection by remoteConnection
