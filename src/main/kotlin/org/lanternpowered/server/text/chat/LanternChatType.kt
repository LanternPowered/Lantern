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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.network.message.Message
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.chat.ChatType

class LanternChatType(key: ResourceKey, val messageProvider: (Text) -> Message) : DefaultCatalogType(key), ChatType
