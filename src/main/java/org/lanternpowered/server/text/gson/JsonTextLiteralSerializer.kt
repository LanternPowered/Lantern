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
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import org.lanternpowered.server.text.translation.TranslationContext
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.lang.reflect.Type

internal class JsonTextLiteralSerializer : JsonTextBaseSerializer<LiteralText>() {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LiteralText {
        if (json.isJsonPrimitive) {
            return Text.of(json.asString)
        }
        json as JsonObject
        val builder = Text.builder(json[TextConstants.TEXT].asString)
        deserialize(json, builder, context)
        return builder.build()
    }

    override fun serialize(src: LiteralText, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
            serializeLiteralText(src, src.content, context, TranslationContext.current().forcesTranslations())

    companion object {

        @JvmStatic
        fun serializeLiteralText(text: Text, content: String, context: JsonSerializationContext, removeComplexity: Boolean): JsonElement {
            val noActionsAndStyle = areActionsAndStyleEmpty(text)
            val children = text.children
            if (noActionsAndStyle) {
                if (children.isEmpty()) {
                    return JsonPrimitive(content)
                    // Try to make the serialized text object less complex,
                    // like text objects nested in a lot of other
                    // text objects, this seems to happen a lot
                } else if (removeComplexity && content.isEmpty()) {
                    return if (children.size == 1) {
                        context.serialize(children[0])
                    } else {
                        context.serialize(children)
                    }
                }
            }
            val json = JsonObject()
            json.addProperty(TextConstants.TEXT, content)
            serialize(json, text, context)
            return json
        }

        /**
         * Gets whether there are no styles or actions applied to the specified [Text].
         *
         * @param text The text
         * @return Are actions and styles empty
         */
        private fun areActionsAndStyleEmpty(text: Text): Boolean {
            return !text.hoverAction.isPresent && !text.clickAction.isPresent && !text.shiftClickAction.isPresent &&
                    text.style.isEmpty && text.color == TextColors.NONE.get()
        }
    }
}