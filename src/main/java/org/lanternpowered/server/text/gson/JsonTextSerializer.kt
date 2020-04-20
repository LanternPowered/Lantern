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
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.ScoreText
import org.spongepowered.api.text.SelectorText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TranslatableText
import java.lang.reflect.Type

internal class JsonTextSerializer : JsonTextBaseSerializer<Text>() {

    override fun serialize(src: Text, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return when (src) {
            is LiteralText -> context.serialize(src, LiteralText::class.java)
            is TranslatableText -> context.serialize(src, TranslatableText::class.java)
            is ScoreText -> context.serialize(src, ScoreText::class.java)
            is SelectorText -> context.serialize(src, SelectorText::class.java)
            else -> throw IllegalStateException("Attempted to serialize an unsupported text type: " + src.javaClass.name)
        }
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Text {
        if (json.isJsonPrimitive)
            return context.deserialize(json, LiteralText::class.java)
        if (json.isJsonArray) {
            val builder = Text.builder()
            builder.append(*context.deserialize(json, Array<Text>::class.java))
            return builder.build()
        }
        val obj = json.asJsonObject
        return when {
            obj.has(TextConstants.TEXT) -> context.deserialize(json, LiteralText::class.java)
            obj.has(TextConstants.TRANSLATABLE) -> context.deserialize(json, TranslatableText::class.java)
            obj.has(TextConstants.SCORE_VALUE) -> context.deserialize(json, ScoreText::class.java)
            obj.has(TextConstants.SELECTOR) -> context.deserialize(json, SelectorText::class.java)
            else -> throw JsonParseException("Unknown text format: $json")
        }
    }
}