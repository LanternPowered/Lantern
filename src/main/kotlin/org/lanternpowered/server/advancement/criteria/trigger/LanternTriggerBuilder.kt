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
package org.lanternpowered.server.advancement.criteria.trigger

import com.google.gson.Gson
import com.google.gson.JsonObject
import ninja.leaping.configurate.gson.GsonConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers
import org.lanternpowered.api.key.resolveNamespacedKey
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.data.persistence.json.JsonDataFormat
import org.spongepowered.api.Sponge
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration
import org.spongepowered.api.advancement.criteria.trigger.Trigger
import org.spongepowered.api.data.persistence.DataSerializable
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.event.advancement.CriterionEvent
import java.io.BufferedReader
import java.io.StringReader
import java.util.function.Consumer

class LanternTriggerBuilder<C : FilteredTriggerConfiguration> : Trigger.Builder<C> {

    private var configType: Class<C>? = null
    private var constructor: ((JsonObject) -> C)? = null
    private var eventHandler: ((CriterionEvent.Trigger<C>) -> Unit)? = null
    private var id: String? = null
    private var name: String? = null

    override fun <T> dataSerializableConfig(dataConfigClass: Class<T>): Trigger.Builder<T>
            where T : FilteredTriggerConfiguration,
                  T : DataSerializable {
        this.configType = dataConfigClass.uncheckedCast()
        this.constructor = DataSerializableConstructor<T>(dataConfigClass).uncheckedCast()
        return this.uncheckedCast()
    }

    private class DataSerializableConstructor<C>(
            private val dataConfigClass: Class<C>
    ) : (JsonObject) -> C
            where C : FilteredTriggerConfiguration,
                  C : DataSerializable {

        override fun invoke(jsonObject: JsonObject): C {
            val builder = Sponge.getDataManager().getBuilder(this.dataConfigClass).get()
            val dataView: DataView = JsonDataFormat.serialize(GSON, jsonObject)
            return builder.build(dataView).get()
        }
    }

    override fun <T : FilteredTriggerConfiguration?> typeSerializableConfig(configClass: Class<T>): Trigger.Builder<T> =
            typeSerializableConfig(configClass, TypeSerializers.getDefaultSerializers())

    override fun <T : FilteredTriggerConfiguration?> typeSerializableConfig(
            configClass: Class<T>, typeSerializerCollection: TypeSerializerCollection
    ): Trigger.Builder<T> {
        this.configType = configClass.uncheckedCast()
        this.constructor = ConfigurateConstructor<C>(TypeToken.of(this.configType!!), typeSerializerCollection)
        return this.uncheckedCast()
    }

    private class ConfigurateConstructor<C : FilteredTriggerConfiguration>(
            private val typeToken: TypeToken<C>,
            private val typeSerializerCollection: TypeSerializerCollection
    ) : (JsonObject) -> C {

        override fun invoke(jsonObject: JsonObject): C {
            val loader = GsonConfigurationLoader.builder()
                    .setSource { BufferedReader(StringReader(GSON.toJson(jsonObject))) }
                    .build()
            val node = loader.load()
            return this.typeSerializerCollection[this.typeToken].deserialize(this.typeToken, node)!!
        }
    }

    override fun <T : FilteredTriggerConfiguration> jsonSerializableConfig(configClass: Class<T>, gson: Gson): Trigger.Builder<T> {
        this.configType = configClass.uncheckedCast()
        this.constructor = JsonConstructor(this.configType!!, gson)
        return this.uncheckedCast()
    }

    override fun <T : FilteredTriggerConfiguration> jsonSerializableConfig(configClass: Class<T>): Trigger.Builder<T> =
            this.jsonSerializableConfig(configClass, GSON)

    private class JsonConstructor<C : FilteredTriggerConfiguration>(
            private val configClass: Class<C>,
            private val gson: Gson
    ) : (JsonObject) -> C {
        override fun invoke(jsonObject: JsonObject): C = this.gson.fromJson(jsonObject, this.configClass)
    }

    override fun emptyConfig(): Trigger.Builder<FilteredTriggerConfiguration.Empty> {
        this.configType = FilteredTriggerConfiguration.Empty::class.java.uncheckedCast()
        this.constructor = EMPTY_TRIGGER_CONFIGURATION_CONSTRUCTOR.uncheckedCast()
        return this.uncheckedCast()
    }

    override fun listener(eventListener: Consumer<CriterionEvent.Trigger<C>>): Trigger.Builder<C> = apply {
        this.eventHandler = { trigger -> eventListener.accept(trigger) }
    }

    override fun id(id: String): Trigger.Builder<C> = apply { this.id = id }
    override fun name(name: String): Trigger.Builder<C> = apply { this.name = name }
    override fun from(value: Trigger<C>): Trigger.Builder<C> = throw UnsupportedOperationException()

    override fun build(): Trigger<C> {
        val id = checkNotNull(this.id) { "The id must be set" }
        val configType = TypeToken.of(checkNotNull(this.configType) { "The configType must be set" })
        val constructor = checkNotNull(this.constructor) { "The constructor must be set" }
        val eventHandler = this.eventHandler ?: {}
        return LanternTrigger<C>(resolveNamespacedKey(id), configType, constructor, eventHandler)
    }

    override fun reset(): Trigger.Builder<C> = apply {
        this.configType = null
        this.constructor = null
        this.eventHandler = null
        this.id = null
        this.name = null
    }

    companion object {
        private val GSON = Gson()
        private val EMPTY_TRIGGER_CONFIGURATION = FilteredTriggerConfiguration.Empty()
        private val EMPTY_TRIGGER_CONFIGURATION_CONSTRUCTOR = { _: JsonObject -> EMPTY_TRIGGER_CONFIGURATION }
    }
}
