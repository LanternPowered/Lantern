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
package org.lanternpowered.server.text.serializer

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.lanternpowered.server.registry.type.text.TextColorRegistry
import org.lanternpowered.server.registry.type.text.TextStyleRegistry
import org.lanternpowered.server.text.format.LanternTextFormat
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextFormat
import org.spongepowered.api.text.format.TextStyle

/**
 * An implementation of [TypeSerializer] to allow serialization of
 * [TextFormat]s directly to a configuration file.
 */
class TextFormatConfigSerializer : TypeSerializer<TextFormat> {

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): TextFormat {
        var color: TextColor = TextColors.NONE.get()
        val colorId = value.getNode(FORMAT_NODE_COLOR).string
        if (colorId != null) {
            color = TextColorRegistry.require(ResourceKey.resolve(colorId))
        }

        var style = TextStyle.of()
        val styleNode = value.getNode(FORMAT_NODE_STYLE)
        for (component in TextStyleRegistry) {
            if (styleNode.getNode(component.key.toString()).boolean) {
                style = style.and(component)
            }
        }

        return LanternTextFormat(color, style)
    }

    override fun serialize(type: TypeToken<*>, obj: TextFormat?, value: ConfigurationNode) {
        if (obj == null) {
            return
        }
        value.getNode(FORMAT_NODE_COLOR).value = obj.color.key.toString()
        val styleNode = value.getNode(FORMAT_NODE_STYLE)
        val composite = obj.style
        TextStyleRegistry.forEach { styleType ->
            styleNode.getNode(styleType.key.toString()).value = composite.contains(styleType)
        }
    }

    companion object {

        private const val FORMAT_NODE_COLOR = "color"
        private const val FORMAT_NODE_STYLE = "style"
    }
}
