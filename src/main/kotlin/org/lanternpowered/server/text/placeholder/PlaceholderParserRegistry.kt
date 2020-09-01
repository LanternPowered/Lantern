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
package org.lanternpowered.server.text.placeholder

import org.lanternpowered.api.key.spongeKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.emptyText
import org.lanternpowered.api.text.placeholder.PlaceholderContext
import org.lanternpowered.api.text.placeholder.PlaceholderParser
import org.lanternpowered.api.text.toText
import org.lanternpowered.api.util.Nameable
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.Locatable
import org.lanternpowered.api.world.WorldManager

val PlaceholderParserRegistry = catalogTypeRegistry<PlaceholderParser> {
    fun register(id: String, fn: (context: PlaceholderContext) -> Text) =
            this.register(LanternPlaceholderParser(spongeKey(id), fn))

    register("name") { context ->
        val obj = context.associatedObject.orNull() as? Nameable
                ?: return@register emptyText()
        obj.name.toText()
    }
    register("current_world") { context ->
        val obj = context.associatedObject.orNull() as? Locatable
                ?: return@register WorldManager.defaultPropertiesKey.formatted.toText()
        obj.serverLocation.worldKey.formatted.toText()
    }
}
