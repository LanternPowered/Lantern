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
package org.lanternpowered.server.network.vanilla.message.type.login

import org.lanternpowered.server.network.message.Message
import java.util.UUID

/**
 * A message send to the client if login is successful.
 *
 * @param uniqueId The unique id of the player
 * @param username The username of the player
 */
data class MessageLoginOutSuccess(val uniqueId: UUID, val username: String) : Message
