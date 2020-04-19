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
package org.lanternpowered.server.data.persistence

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.lanternpowered.server.data.translator.ConfigurateTranslator
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataQuery.of
import org.spongepowered.api.data.persistence.DataTranslator
import org.spongepowered.api.data.persistence.DataView
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
