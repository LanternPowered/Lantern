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

import org.lanternpowered.api.text.Text
import org.lanternpowered.server.network.message.Packet

/**
 * A message to send a disconnect reason to the client.
 *
 * @param reason The reason of the disconnection
 */
data class DisconnectPacket(val reason: Text) : Packet
