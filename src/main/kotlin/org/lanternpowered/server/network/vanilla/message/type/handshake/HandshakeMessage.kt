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
package org.lanternpowered.server.network.vanilla.message.type.handshake

import org.lanternpowered.server.network.message.Message

/**
 * The initial handshake message.
 *
 * @property nextState The next protocol state
 * @property hostname The host name that was used to join the server
 * @property protocolVersion The protocol version of the client.
 */
data class HandshakeMessage(val nextState: Int, val hostname: String, val port: Int, val protocolVersion: Int) : Message
