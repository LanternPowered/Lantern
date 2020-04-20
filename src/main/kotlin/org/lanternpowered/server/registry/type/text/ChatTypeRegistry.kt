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
package org.lanternpowered.server.registry.type.text

import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle
import org.lanternpowered.server.text.chat.LanternChatType
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.chat.ChatType

val ChatTypeRegistry = catalogTypeRegistry<ChatType> {
    fun register(id: String, messageProvider: (Text) -> Message) =
            register(LanternChatType(CatalogKey.minecraft(id), messageProvider))

    register("chat") { text -> MessagePlayOutChatMessage(text, MessagePlayOutChatMessage.Type.CHAT) }
    register("action_bar") { text -> MessagePlayOutTitle.SetActionbarTitle(text) }
    register("system") { text -> MessagePlayOutChatMessage(text, MessagePlayOutChatMessage.Type.SYSTEM) }
}
