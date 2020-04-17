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
package org.lanternpowered.server.state

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.ImmutableTable
import com.google.common.collect.Lists
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.ext.immutableMapBuilderOf
import org.lanternpowered.api.ext.immutableSetBuilderOf
import org.lanternpowered.api.ext.toImmutableList
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.registry.CatalogRegistryModule
import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateContainer
import org.spongepowered.api.state.StateProperty
import java.util.LinkedHashMap
import java.util.Optional

abstract class AbstractStateContainer<S : State<S>>(
        baseKey: CatalogKey, stateProperties: Iterable<StateProperty<*>>, constructor: (StateBuilder<S>) -> S
) : StateContainer<S> {

    // The lookup to convert between key <--> state property
    val keysToProperty: ImmutableMap<Key<out Value<*>>, StateProperty<*>>
    private val validStates: ImmutableList<S>

    init {
        val properties = stateProperties.toMutableList()
        properties.sortBy { property -> property.getName() }

        val keysToPropertyBuilder = ImmutableMap.builder<Key<out Value<*>>, StateProperty<*>>()
        for (property in properties) {
            keysToPropertyBuilder.put((property as IStateProperty<*,*>).valueKey, property)
        }
        this.keysToProperty = keysToPropertyBuilder.build()

        val cartesianProductInput = properties.map { property ->
            property.getPossibleValues().map { comparable ->
                @Suppress("UNCHECKED_CAST")
                val valueKey = (property as IStateProperty<*,*>).valueKey as Key<Value<Any>>
                val value = Value.immutableOf(valueKey, comparable as Any).asImmutable()
                Triple(property, comparable, value)
            }
        }
        val cartesianProduct = Lists.cartesianProduct(cartesianProductInput)
        val stateBuilders = mutableListOf<LanternStateBuilder<S>>()

        // A map with as the key the property values map and as value the state
        val stateByValuesMap = LinkedHashMap<Map<*, *>, S>()

        for ((internalId, list) in cartesianProduct.withIndex()) {
            val stateValuesBuilder = immutableMapBuilderOf<StateProperty<*>, Comparable<*>>()
            val immutableValuesBuilder = immutableSetBuilderOf<Value.Immutable<*>>()

            for ((property, comparable, value) in list) {
                stateValuesBuilder.put(property, comparable)
                immutableValuesBuilder.add(value)
            }

            val stateValues = stateValuesBuilder.build()
            val immutableValues = immutableValuesBuilder.build()
            val key = buildKey(baseKey, stateValues)
            val dataContainer = buildDataContainer(baseKey, stateValues)

            @Suppress("LeakingThis")
            stateBuilders += LanternStateBuilder(key, dataContainer, this, stateValues, immutableValues, internalId)
        }

        // There are no properties, so just add
        // the single state of this container
        if (properties.isEmpty()) {
            val dataContainer = buildDataContainer(baseKey, ImmutableMap.of())

            @Suppress("LeakingThis")
            stateBuilders += LanternStateBuilder(baseKey, dataContainer, this, ImmutableMap.of(), ImmutableSet.of(), 0)
        }

        this.validStates = stateBuilders.map {
            val state = constructor(it)
            stateByValuesMap[it.stateValues] = state
            state
        }.toImmutableList()

        for (state in this.validStates) {
            val tableBuilder = ImmutableTable.builder<StateProperty<*>, Comparable<*>, S>()
            for (property in properties) {
                @Suppress("UNCHECKED_CAST")
                property as StateProperty<Comparable<Comparable<*>>>
                for (value in property.possibleValues) {
                    if (value == state.getStateProperty(property).get())
                        continue
                    val valueByProperty = HashMap<StateProperty<*>, Any>(state.statePropertyMap)
                    valueByProperty[property] = value
                    tableBuilder.put(property, value, checkNotNull(stateByValuesMap[valueByProperty]))
                }
            }
            @Suppress("UNCHECKED_CAST")
            (state as AbstractState<S,*>).propertyValueTable = tableBuilder.build()
        }

        // Call the completion
        for (stateBuilder in stateBuilders) {
            stateBuilder.whenCompleted.forEach { it() }
        }
    }

    companion object {

        private val NAME = DataQuery.of("Name")
        private val PROPERTIES = DataQuery.of("Properties")

        fun <T, S : State<S>> deserializeState(dataView: DataView, registry: CatalogRegistryModule<T>):
                S where T : CatalogType, T : StateContainer<S> {
            val id = dataView.getString(NAME).get()
            val catalogType = registry.get(CatalogKey.resolve(id)).get()

            var state = catalogType.defaultState
            val properties = dataView.getView(PROPERTIES).orElse(null)
            if (properties != null) {
                for ((key, rawValue) in properties.getValues(false)) {
                    val stateProperty = state.getStatePropertyByName(key.toString()).orElse(null)
                    if (stateProperty != null) {
                        val value = stateProperty.parseValue(rawValue.toString()).orElse(null)
                        if (value != null) {
                            @Suppress("UNCHECKED_CAST")
                            stateProperty as StateProperty<Comparable<Comparable<*>>>
                            val newState = state.withStateProperty(stateProperty, value.uncheckedCast()).orElse(null)
                            if (newState != null) {
                                state = newState
                            }
                        }
                    }
                }
            }

            return state
        }
    }

    private fun buildDataContainer(baseKey: CatalogKey, values: Map<StateProperty<*>, Comparable<*>>): DataContainer {
        val dataContainer = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
        dataContainer[NAME] = baseKey.toString()

        if (values.isEmpty())
            return dataContainer

        val propertiesView = dataContainer.createView(PROPERTIES)
        for ((property, comparable) in values) {
            propertiesView[DataQuery.of(property.getName())] = comparable
        }

        return dataContainer
    }

    private fun buildKey(baseKey: CatalogKey, values: Map<StateProperty<*>, Comparable<*>>): CatalogKey {
        if (values.isEmpty())
            return baseKey

        val builder = StringBuilder()
        builder.append(baseKey.value).append('[')

        val propertyValues = mutableListOf<String>()
        for ((property, comparable) in values) {
            val value = if (comparable is Enum) comparable.name else comparable.toString()
            propertyValues.add(property.getName() + '=' + value.toLowerCase())
        }

        builder.append(propertyValues.joinToString(separator = ","))
        builder.append(']')

        return CatalogKey(baseKey.namespace, builder.toString())
    }

    override fun getValidStates(): ImmutableList<S> = this.validStates

    override fun getDefaultState(): S = this.validStates[0]

    override fun getStateProperties(): Collection<StateProperty<*>> = this.keysToProperty.values

    override fun getStatePropertyByName(statePropertyId: String): Optional<StateProperty<*>>
            = this.defaultState.getStatePropertyByName(statePropertyId)
}
