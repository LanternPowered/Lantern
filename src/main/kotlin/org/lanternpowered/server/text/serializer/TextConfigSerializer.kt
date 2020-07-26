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
import ninja.leaping.configurate.gson.GsonConfigurationLoader
import ninja.leaping.configurate.loader.HeaderMode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.persistence.InvalidDataException
import org.spongepowered.api.data.persistence.Queries
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
            return container.get(Queries.JSON).map { json -> JsonTextSerializer.deserialize(json.toString()) }
        } catch (e: TextParseException) {
            throw InvalidDataException(e)
        }
    }
}
