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

sealed class ChangeAdvancementTreeMessage : Message {

    /**
     * When a client switches between advancement trees (tabs).
     *
     * @param id The id of the opened advancement tree.
     */
    data class Open(val id: String) : ChangeAdvancementTreeMessage()

    /**
     * When a client closes the advancements.
     */
    object Close : ChangeAdvancementTreeMessage()
}
