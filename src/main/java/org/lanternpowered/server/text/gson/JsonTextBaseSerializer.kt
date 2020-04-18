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
package org.lanternpowered.server.text.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.get
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import org.spongepowered.api.text.action.ShiftClickAction.InsertText
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyle
import java.util.Optional

internal abstract class JsonTextBaseSerializer<T : Text> : JsonSerializer<T>, JsonDeserializer<T> {

    protected fun deserialize(json: JsonObject, builder: Text.Builder, context: JsonDeserializationContext) {
        json[TextConstants.COLOR]?.let { element ->
            CatalogRegistry.get<TextColor>(CatalogKey.resolve(element.asString))?.let { builder.color(it) }
        }

        var style = builder.style
        fun style(key: String, fn: TextStyle.(Boolean) -> TextStyle) {
            json[key]?.let { element -> style = style.fn(element.asBoolean) }
        }
        style(TextConstants.BOLD, TextStyle::bold)
        style(TextConstants.ITALIC, TextStyle::italic)
        style(TextConstants.UNDERLINE, TextStyle::underline)
        style(TextConstants.STRIKETHROUGH, TextStyle::strikethrough)
        style(TextConstants.OBFUSCATED, TextStyle::obfuscated)
        builder.style(style)

        val children = json.getAsJsonArray(TextConstants.CHILDREN)
        if (children != null)
            builder.append(context.deserialize<Array<Text>>(children, Array<Text>::class.java).asList())

        json[TextConstants.CLICK_EVENT]?.let { element ->
            element as JsonObject
            val jsonEventAction = element.getAsJsonPrimitive(TextConstants.EVENT_ACTION)
            val jsonEventValue = element.getAsJsonPrimitive(TextConstants.EVENT_VALUE)
            if (jsonEventAction != null && jsonEventValue != null) {
                val action = jsonEventAction.asString
                val value = jsonEventValue.asString
                val clickAction = JsonTextEventHelper.parseClickAction(action, value)
                if (clickAction != null)
                    builder.onClick(clickAction)
            }
        }

        json[TextConstants.HOVER_EVENT]?.let { element ->
            element as JsonObject
            val jsonEventAction = element.getAsJsonPrimitive(TextConstants.EVENT_ACTION)
            val jsonEventValue = element.getAsJsonPrimitive(TextConstants.EVENT_VALUE)
            if (jsonEventAction != null && jsonEventValue != null) {
                val action = jsonEventAction.asString
                val value = jsonEventValue.asString
                builder.onHover(JsonTextEventHelper.parseHoverAction(action, value))
            }
        }

        json[TextConstants.INSERTION]?.let { element ->
            builder.onShiftClick(TextActions.insertText(element.asString))
        }
    }

    fun serialize(json: JsonObject, text: Text, context: JsonSerializationContext) {
        val color = text.color
        if (color !== TextColors.NONE.get())
            json.addProperty(TextConstants.COLOR, color.key.value)

        val style = text.style
        fun style(key: String, fn: TextStyle.() -> Optional<Boolean>) {
            style.fn().ifPresent { value -> json.addProperty(key, value) }
        }
        style(TextConstants.BOLD, TextStyle::hasBold)
        style(TextConstants.ITALIC, TextStyle::hasItalic)
        style(TextConstants.UNDERLINE, TextStyle::hasUnderline)
        style(TextConstants.STRIKETHROUGH, TextStyle::hasStrikethrough)
        style(TextConstants.OBFUSCATED, TextStyle::hasObfuscated)

        val children = text.children
        if (children.isNotEmpty())
            json.add(TextConstants.CHILDREN, context.serialize(children.toTypedArray()))

        text.clickAction.ifPresent { clickAction: ClickAction<*> ->
            val raw = JsonTextEventHelper.raw(clickAction)
            val jsonEvent = JsonObject()
            jsonEvent.addProperty(TextConstants.EVENT_ACTION, raw.action)
            jsonEvent.addProperty(TextConstants.EVENT_VALUE, raw.value)
            json.add(TextConstants.CLICK_EVENT, jsonEvent)
        }

        text.hoverAction.ifPresent { clickAction: HoverAction<*> ->
            val raw = JsonTextEventHelper.raw(clickAction)
            val jsonEvent = JsonObject()
            jsonEvent.addProperty(TextConstants.EVENT_ACTION, raw.action)
            jsonEvent.addProperty(TextConstants.EVENT_VALUE, raw.value)
            json.add(TextConstants.HOVER_EVENT, jsonEvent)
        }

        text.shiftClickAction.ifPresent { shiftClickAction: ShiftClickAction<*>? ->
            if (shiftClickAction is InsertText) {
                json.addProperty(TextConstants.INSERTION, shiftClickAction.result)
            }
        }
    }
}