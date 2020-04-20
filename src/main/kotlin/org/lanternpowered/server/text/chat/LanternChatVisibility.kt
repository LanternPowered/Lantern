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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.text.chat.ChatType
import org.spongepowered.api.text.chat.ChatVisibility
import org.spongepowered.api.text.translation.Translatable

class LanternChatVisibility(
        key: CatalogKey, private val chatTypePredicate: (ChatType) -> Boolean
) : DefaultCatalogType(key), ChatVisibility, Translatable by Translated("options.chat.visibility.${key.value}") {

    override fun isVisible(chatType: ChatType): Boolean = this.chatTypePredicate(chatType)
}
