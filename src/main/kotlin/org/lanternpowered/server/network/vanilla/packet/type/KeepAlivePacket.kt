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
package org.lanternpowered.server.network.vanilla.packet.type

import org.lanternpowered.server.network.message.Packet

/**
 * Send between the client and server to keep the connection
 * alive and to determine the ping.
 *
 * @param time The time the message was sent
 */
data class KeepAlivePacket(val time: Long) : Packet
