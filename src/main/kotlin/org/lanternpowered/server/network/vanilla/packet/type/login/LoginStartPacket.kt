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
package org.lanternpowered.server.network.vanilla.packet.type.login

import org.lanternpowered.server.network.message.Packet

/**
 * A message send by the client to initiate the login process.
 *
 * @param username The username of the player that wants to join the server
 */
data class LoginStartPacket(val username: String) : Packet
