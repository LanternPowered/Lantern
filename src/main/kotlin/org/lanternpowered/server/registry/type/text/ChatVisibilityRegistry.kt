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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.audience.MessageType
import org.lanternpowered.api.entity.player.chat.ChatVisibility
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.text.chat.LanternChatVisibility

val ChatVisibilityRegistry = internalCatalogTypeRegistry<ChatVisibility> {
    fun register(id: String, isChatVisible: (MessageType) -> Boolean) =
            register(LanternChatVisibility(ResourceKey.minecraft(id), isChatVisible))

    register("full") { true }
    register("system") { type -> type == MessageType.SYSTEM }
    register("hidden") { false }
}
