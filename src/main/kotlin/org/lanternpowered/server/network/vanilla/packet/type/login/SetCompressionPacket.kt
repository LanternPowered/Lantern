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

import org.lanternpowered.server.network.packet.Packet

/**
 * The set compression message, this can be send in the
 * LOGIN protocol state.
 *
 * @property threshold The threshold, it is the max size of a packet before its compressed. Use -1 to disable compression.
 */
data class SetCompressionPacket(val threshold: Int) : Packet
