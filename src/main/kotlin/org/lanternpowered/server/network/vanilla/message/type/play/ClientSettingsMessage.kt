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
package org.lanternpowered.server.network.vanilla.message.type.play

import org.lanternpowered.server.network.message.Message
import org.spongepowered.api.data.type.HandPreference
import org.spongepowered.api.text.chat.ChatVisibility
import java.util.Locale

data class ClientSettingsMessage(
        val locale: Locale,
        val viewDistance: Int,
        val chatVisibility: ChatVisibility,
        val dominantHand: HandPreference,
        val enableColors: Boolean,
        val skinPartsBitPattern: Int
) : Message
