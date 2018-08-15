/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.text.serializer

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.text.format.LanternTextFormat
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextFormat
import org.spongepowered.api.text.format.TextStyle
import org.spongepowered.api.text.format.TextStyles

/**
 * An implementation of [TypeSerializer] to allow serialization of
 * [TextFormat]s directly to a configuration file.
 */
class TextFormatConfigSerializer : TypeSerializer<TextFormat> {

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): TextFormat {
        var color: TextColor = TextColors.NONE
        val colorId = value.getNode(FORMAT_NODE_COLOR).string
        if (colorId != null) {
            color = catalogOf(CatalogKey.resolve(colorId)) ?: throw ObjectMappingException("Color not found: $colorId")
        }

        var style = TextStyles.NONE
        val styleNode = value.getNode(FORMAT_NODE_STYLE)
        for (component in allCatalogsOf<TextStyle.Base>()) {
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
        allCatalogsOf<TextStyle.Base>().forEach { styleNode.getNode(it.key.toString()).value = composite.contains(it) }
    }

    companion object {

        private const val FORMAT_NODE_COLOR = "color"
        private const val FORMAT_NODE_STYLE = "style"
    }
}
