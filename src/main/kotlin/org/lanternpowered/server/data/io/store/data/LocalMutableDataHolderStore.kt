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
package org.lanternpowered.server.data.io.store.data

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.ElementKeyRegistration
import org.lanternpowered.server.data.LocalMutableDataHolder
import org.lanternpowered.server.data.io.store.ObjectStore
import org.lanternpowered.server.data.io.store.SimpleValueContainer
import org.lanternpowered.server.data.key.ValueKeyRegistryModule
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.game.registry.type.data.DataSerializerRegistry
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.Value

open class LocalMutableDataHolderStore<H : LocalMutableDataHolder> : ObjectStore<H> {

    override fun deserialize(holder: H, dataView: DataView) {
        val simpleValueContainer = SimpleValueContainer(mutableMapOf())
        deserializeValues(holder, simpleValueContainer, dataView)

        val valuesView = dataView.getView(VALUES).orElse(null)
        if (valuesView != null) {
            val context = DataSerializerRegistry.typeSerializerContext

            for ((query, raw) in valuesView.getValues(false)) {
                val catalogKey = CatalogKeys.resolve(query.toString())
                val key = ValueKeyRegistryModule[catalogKey].orNull().uncheckedCast<Key<Value<Any>>?>()
                if (key == null) {
                    Lantern.getLogger().warn(
                            "Unable to deserialize the data value with key: $catalogKey because it doesn't exist.")
                } else {
                    val elementType = key.elementToken.uncheckedCast<TypeToken<Any>>()
                    val serializer = context.serializers.getTypeSerializer<Any, Any>(elementType).orNull()
                    if (serializer == null) {
                        Lantern.getLogger().warn("Unable to deserialize the data key value: ${key.key}, "
                                + "no supported deserializer exists.")
                    } else {
                        if (simpleValueContainer[key].isPresent) {
                            Lantern.getLogger().warn("Duplicate usage of the key ${key.key} for value container ${holder.javaClass.name}")
                        } else {
                            simpleValueContainer[key] = serializer.deserialize(elementType, context, raw)
                        }
                    }
                }
            }

            val keyRegistry = holder.keyRegistry
            for ((key, value) in simpleValueContainer.values) {
                val registration = keyRegistry.getAsElement(key.uncheckedCast<Key<Value<Any>>>())
                if (registration != null) {
                    registration.set(value)
                } else {
                    Lantern.getLogger().debug("Attempted to offer a unsupported value with key \"${key.key}\" to the holder" +
                            " ${holder.javaClass.name}")
                }
            }
        }
    }

    override fun serialize(holder: H, dataView: DataView) {
        val keyRegistry = holder.keyRegistry

        val simpleValueContainer = SimpleValueContainer(mutableMapOf())
        for (registration in keyRegistry.registrations) {
            if (registration !is ElementKeyRegistration) continue
            val element = registration.get()
            if (element != null) {
                val key = registration.key.uncheckedCast<Key<Value<Any>>>()
                simpleValueContainer[key] = element
            }
        }

        // Serialize the values, all written values will be removed from
        // the simple value container
        serializeValues(holder, simpleValueContainer, dataView)

        val values = simpleValueContainer.values
        if (values.isNotEmpty()) {
            val valuesView = dataView.createView(VALUES)
            val context = DataSerializerRegistry.typeSerializerContext

            for ((key, value) in values) {
                val elementType = key.elementToken.uncheckedCast<TypeToken<Any>>()
                val serializer = context.serializers.getTypeSerializer<Any, Any>(elementType).orNull()

                if (serializer == null) {
                    Lantern.getLogger().warn("Unable to serialize the data key value: ${key.key}")
                } else {
                    valuesView.set(DataQuery.of(key.key.toString()), serializer.serialize(elementType, context, value))
                }
            }
            if (valuesView.isEmpty) {
                dataView.remove(VALUES)
            }
        }
    }

    /**
     * Serializes all the values of the [SimpleValueContainer] and puts
     * them into the [DataView].
     *
     * @param valueContainer The value container
     * @param dataView The data view
     */
    open fun serializeValues(holder: H, valueContainer: SimpleValueContainer, dataView: DataView) {}

    /**
     * Deserializers all the values from the [DataView]
     * into the [SimpleValueContainer].
     *
     * @param valueContainer The value container
     * @param dataView The data view
     */
    open fun deserializeValues(holder: H, valueContainer: SimpleValueContainer, dataView: DataView) {}

    companion object {

        private val VALUES = DataQuery.of("Values")
    }
}
