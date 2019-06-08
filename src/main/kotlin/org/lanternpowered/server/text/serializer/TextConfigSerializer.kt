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
import ninja.leaping.configurate.gson.GsonConfigurationLoader
import ninja.leaping.configurate.loader.HeaderMode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.InvalidDataException
import org.spongepowered.api.data.persistence.Queries
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextParseException
import org.spongepowered.api.text.serializer.TextSerializers
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import java.util.Optional

/**
 * Represents a [TypeSerializer] for [Text] objects. Serialization
 * is handled by serializing the text to String with the
 * [TextSerializers.JSON] serializer, loading the String into a
 * [GsonConfigurationLoader], and setting the value of the
 * [ConfigurationNode] to the root node of the GsonConfigurationLoader.
 * Although JSON is used for serialization internally, this has no effect on
 * the actual configuration format the developer chooses to use.
 */
class TextConfigSerializer : AbstractDataBuilder<Text>(Text::class.java, 1), TypeSerializer<Text> {

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

        return Sponge.getDataManager().deserialize(Text::class.java, DataContainer.createNew().set(Queries.JSON, writer.toString())).get()
    }

    override fun serialize(type: TypeToken<*>, obj: Text?, value: ConfigurationNode) {
        if (obj == null) {
            return
        }

        val json = obj.toContainer().get(Queries.JSON).get() as String
        val gsonLoader = GsonConfigurationLoader.builder()
                .setSource { BufferedReader(StringReader(json)) }
                .build()

        try {
            value.value = gsonLoader.load()
        } catch (e: IOException) {
            throw ObjectMappingException(e)
        }
    }

    override fun buildContent(container: DataView): Optional<Text> {
        try {
            return container.get(Queries.JSON).map { json -> TextSerializers.JSON.deserialize(json.toString()) }
        } catch (e: TextParseException) {
            throw InvalidDataException(e)
        }
    }
}
