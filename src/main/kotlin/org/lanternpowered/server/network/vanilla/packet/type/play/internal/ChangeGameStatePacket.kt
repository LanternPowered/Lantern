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
package org.lanternpowered.server.network.vanilla.packet.type.play.internal

import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.SetGameModePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateWorldSkyPacket

/**
 * This message should not be used directly in the server implementation,
 * this is only for internal purposes used by other message types in the
 * processing. Messages like: [UpdateWorldSkyPacket],
 * [SetGameModePacket], etc.
 *
 * @property type the type
 * @property value the value
 */
data class ChangeGameStatePacket(val type: Int, val value: Float) : Packet
