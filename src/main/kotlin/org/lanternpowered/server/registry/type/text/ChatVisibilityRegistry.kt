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

import org.lanternpowered.api.audience.MessageType
import org.lanternpowered.api.entity.player.chat.ChatVisibility
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.registry.internalCatalogTypeRegistry

val ChatVisibilityRegistry = internalCatalogTypeRegistry<ChatVisibility> {
    fun register(id: String, isChatVisible: (MessageType) -> Boolean) =
            register(LanternChatVisibility(NamespacedKey.minecraft(id), translatableTextOf("options.chat.visibility.$id"), isChatVisible))

    register("full") { true }
    register("system") { type -> type == MessageType.SYSTEM }
    register("hidden") { false }
}

private class LanternChatVisibility(
        key: NamespacedKey, text: Text, private val chatTypePredicate: (MessageType) -> Boolean
) : DefaultCatalogType(key), ChatVisibility, TextRepresentable by text {

    override fun isVisible(type: net.kyori.adventure.audience.MessageType): Boolean = this.chatTypePredicate(type)
}
