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

import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.text.chat.LanternChatVisibility
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.text.chat.ChatType
import org.spongepowered.api.text.chat.ChatTypes
import org.spongepowered.api.text.chat.ChatVisibility

val ChatVisibilityRegistry = internalCatalogTypeRegistry<ChatVisibility> {
    fun register(id: String, isChatVisible: (ChatType) -> Boolean) =
            register(LanternChatVisibility(CatalogKey.minecraft(id), isChatVisible))

    register("full") { true }
    register("system") { type -> type == ChatTypes.SYSTEM.get() || type == ChatTypes.ACTION_BAR.get() }
    register("hidden") { false }
}
