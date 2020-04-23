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
package org.lanternpowered.server.network.vanilla.message.type.play.internal

import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.vanilla.message.type.play.SetGameModeMessage
import org.lanternpowered.server.network.vanilla.message.type.play.UpdateWorldSkyMessage

/**
 * This message should not be used directly in the server implementation,
 * this is only for internal purposes used by other message types in the
 * processing. Messages like: [UpdateWorldSkyMessage],
 * [SetGameModeMessage], etc.
 *
 * @property type the type
 * @property value the value
 */
data class ChangeGameStateMessage(val type: Int, val value: Float) : Message
