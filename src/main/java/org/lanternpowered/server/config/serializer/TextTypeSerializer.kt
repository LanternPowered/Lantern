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