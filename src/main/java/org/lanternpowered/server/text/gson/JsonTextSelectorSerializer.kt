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