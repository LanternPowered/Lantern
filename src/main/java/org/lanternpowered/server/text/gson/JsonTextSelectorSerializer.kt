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
package org.lanternpowered.server.text.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import org.spongepowered.api.text.SelectorText
import org.spongepowered.api.text.selector.Selector
import java.lang.reflect.Type

internal class JsonTextSelectorSerializer : JsonTextBaseSerializer<SelectorText>() {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SelectorText {
        val obj = json.asJsonObject
        val selector = Selector.parse(obj[TextConstants.SELECTOR].asString)
        val builder = SelectorText.builder().selector(selector)
        deserialize(obj, builder, context)
        return builder.build()
    }

    override fun serialize(src: SelectorText, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val obj = JsonObject()
        obj.addProperty(TextConstants.SELECTOR, src.selector.toPlain())
        serialize(obj, src, context)
        return obj
    }
}