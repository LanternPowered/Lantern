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
package org.lanternpowered.server.data.persistence

import org.spongepowered.api.data.DataQuery.of

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.lanternpowered.server.data.translator.ConfigurateTranslator
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.DataView
import org.spongepowered.api.data.persistence.DataTranslator
import org.spongepowered.api.data.persistence.InvalidDataException

class DataTranslatorTypeSerializer<T : Any> private constructor(private val dataTranslator: DataTranslator<T>) : TypeSerializer<T> {

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): T? {
        return try {
            val dataContainer = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
            ConfigurateTranslator.instance().addTo(value, dataContainer)
            this.dataTranslator.translate(dataContainer)
        } catch (e: InvalidDataException) {
            // Since the value in the config node might be null, return null if an error occurs.
            null
        }
    }

    override fun serialize(type: TypeToken<*>, obj: T?, value: ConfigurationNode) {
        try {
            value.value = this.dataTranslator.translate(checkNotNull(obj)).getMap(of()).get()
        } catch (e: InvalidDataException) {
            throw ObjectMappingException("Could not serialize. Data was invalid.", e)
        }
    }

    companion object {

        @JvmStatic
        fun <T : Any> from(dataTranslator: DataTranslator<T>) = DataTranslatorTypeSerializer(dataTranslator)
    }
}
