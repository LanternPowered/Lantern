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
package org.lanternpowered.server.text.chat

import net.kyori.adventure.audience.MessageType
import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.entity.living.player.chat.ChatVisibility

class LanternChatVisibility(
        key: NamespacedKey, private val chatTypePredicate: (MessageType) -> Boolean
) : DefaultCatalogType(key), ChatVisibility, Translatable by Translated("options.chat.visibility.${key.value}") {

    override fun isVisible(type: MessageType): Boolean = this.chatTypePredicate(type)
}
