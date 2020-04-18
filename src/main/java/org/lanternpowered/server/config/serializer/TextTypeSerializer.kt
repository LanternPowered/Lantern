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
package org.lanternpowered.server.config.serializer

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.gson.GsonConfigurationLoader
import ninja.leaping.configurate.loader.HeaderMode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.spongepowered.api.text.Text
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter

class TextTypeSerializer : TypeSerializer<Text> {

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): Text {
        val writer = StringWriter()
        val gsonLoader = GsonConfigurationLoader.builder()
                .setIndent(0)
                .setSink { BufferedWriter(writer) }
                .setHeaderMode(HeaderMode.NONE)
                .build()
        try {
            gsonLoader.save(value)
        } catch (e: IOException) {
            throw ObjectMappingException(e)
        }
        return JsonTextSerializer.deserialize(writer.toString())
    }

    override fun serialize(type: TypeToken<*>, obj: Text?, value: ConfigurationNode) {
        if (obj == null) {
            value.value = null
            return
        }
        val json: String = JsonTextSerializer.serialize(obj)
        val gsonLoader = GsonConfigurationLoader.builder()
                .setSource { BufferedReader(StringReader(json)) }
                .build()
        try {
            value.value = gsonLoader.load()
        } catch (e: IOException) {
            throw ObjectMappingException(e)
        }
    }
}