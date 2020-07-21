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
package org.lanternpowered.server.network.vanilla.packet.type.play

import org.lanternpowered.api.entity.player.chat.ChatVisibility
import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.data.type.HandPreference
import java.util.Locale

data class ClientSettingsPacket(
        val locale: Locale,
        val viewDistance: Int,
        val chatVisibility: ChatVisibility,
        val dominantHand: HandPreference,
        val enableColors: Boolean,
        val skinPartsBitPattern: Int
) : Packet
