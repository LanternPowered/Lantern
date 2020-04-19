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
import org.lanternpowered.api.util.optional.orNull
import org.spongepowered.api.text.TextTemplate

class TextTemplateArgConfigSerializer : TypeSerializer<TextTemplate.Arg> {

    override fun serialize(type: TypeToken<*>, obj: TextTemplate.Arg?, value: ConfigurationNode) {
        if (obj == null) {
            return
        }
        value.getNode(TextTemplateConfigSerializer.NODE_DEF_VAL).value = obj.defaultValue.orNull()
        value.getNode(TextTemplateConfigSerializer.NODE_OPT).value = obj.isOptional
    }

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode) = throw IllegalStateException()
}
