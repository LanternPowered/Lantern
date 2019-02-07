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

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.ImmutableTable
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.catalog.AbstractCatalogType
import org.lanternpowered.server.data.IImmutableDataHolderBase
import org.lanternpowered.server.data.IImmutableValueHolder
import org.lanternpowered.server.data.property.IStorePropertyHolder
import org.lanternpowered.server.data.value.LanternMutableValue
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator
import org.spongepowered.api.data.merge.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.Value.Immutable
import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateContainer
import org.spongepowered.api.state.StateProperty
import org.spongepowered.api.util.Cycleable
import java.util.Optional
import java.util.function.Function
import java.util.function.Predicate

@Suppress("UNCHECKED_CAST")
abstract class AbstractState<S : State<S>, C : StateContainer<S>>(data: StateData<S>) : AbstractCatalogType(), IState<S>, IStorePropertyHolder,
        IImmutableDataHolderBase<S> {

    private val key: org.lanternpowered.api.catalog.CatalogKey

    // The container this state is linked to
    override val stateContainer: C

    // All the values of this state
    private val stateValues: ImmutableMap<StateProperty<*>, Comparable<*>>

    // The lookup to convert between key <--> trait
    private val keysToProperty: ImmutableMap<Key<out Value<*>>, StateProperty<*>>

    // A lookup table to get a specific state when you would change a value
    private val propertyValueTable: ImmutableTable<StateProperty<*>, Comparable<*>, S>

    // The internal id of this state within the state container
    override val internalId: Int

    // A list with all the values of this state
    private val values: ImmutableSet<Immutable<*>>

    // A cache to reuse constructed data manipulators
    private val immutableContainerCache = IImmutableValueHolder.ImmutableContainerCache()

    // Serialized data container
    private val dataContainer: DataContainer

    init {
        data as LanternStateData<S>

        this.key = data.key
        this.stateValues = data.stateProperties
        this.stateContainer = data.stateContainer as C
        this.internalId = data.internalId
        this.dataContainer = data.dataContainer

        keysToProperty = null as ImmutableMap<Key<out Value<*>>, StateProperty<*>>
        propertyValueTable = ImmutableTable.of()
        values = ImmutableSet.of()
    }

    override fun getContainerCache() = this.immutableContainerCache

    override fun <T : Comparable<T>> getStateProperty(stateProperty: StateProperty<T>): Optional<T> {
        return (this.stateValues[stateProperty] as? T).optional()
    }

    override fun <T : Comparable<T>, V : T> withStateProperty(stateProperty: StateProperty<T>, value: V): Optional<S> {
        return Optional.ofNullable(this.propertyValueTable.row(stateProperty)?.get(value))
    }

    override fun getStatePropertyByName(statePropertyId: String): Optional<StateProperty<*>> {
        return Optional.empty()
    }

    override fun <T : Comparable<T>> cycleStateProperty(stateProperty: StateProperty<T>): Optional<S> {
        var value = this.stateValues[stateProperty] as? T ?: return Optional.empty()

        if (value is Cycleable<*>) {
            var last = value
            var next: T
            while (true) {
                next = (last as Cycleable<*>).cycleNext() as T
                if (next === value) {
                    // We cycled completely, abort
                    return Optional.of(this as S)
                }
                // Check if the next value is actually supported
                if (stateProperty.predicate.test(next)) {
                    value = next
                    break
                }
                last = next
            }
        } else {
            val list = ArrayList<T>(stateProperty.possibleValues)
            list.sort()
            val it = list.iterator()
            while (it.hasNext()) {
                if (it.next() === value) {
                    value = if (it.hasNext()) {
                        it.next()
                    } else {
                        list[0]
                    }
                }
            }
        }

        return Optional.of(this.propertyValueTable.row(stateProperty)[value] as S)
    }

    override fun <T : Cycleable<T>> cycleValue(key: Key<out Value<T>>): Optional<S> {
        val value = get(key).orNull() ?: return Optional.empty()

        var last = value
        var next: T
        while (true) {
            next = (last as Cycleable<*>).cycleNext() as T
            if (next === value) {
                // We cycled completely, abort
                return Optional.of(this as S)
            }
            // Check if the next value is actually supported
            val state = with(key, next)
            if (state.isPresent) {
                return state
            }
            last = next
        }
    }

    override fun getStateProperties(): Collection<StateProperty<*>> = this.stateValues.keys
    override fun getStatePropertyValues(): Collection<*> = this.stateValues.values
    override fun getStatePropertyMap(): Map<StateProperty<*>, *> = this.stateValues
    override fun getKey() = this.key
    override fun getContentVersion() = 1
    override fun toContainer(): DataContainer = this.dataContainer.copy()

    override fun <E> transform(key: Key<out Value<E>>, function: Function<E, E>): Optional<S> {
        val property = (this.keysToProperty[key] as? AbstractStateProperty<*, E>) ?: return Optional.empty()
        val transformer = property.keyValueTransformer as StateKeyValueTransformer<Comparable<Any>, E>

        val currentStateValue = this.stateValues[property]
        val currentKeyValue = transformer.toKeyValue(currentStateValue as Comparable<Any>)

        val newKeyValue = function.apply(currentKeyValue)
        if (newKeyValue === currentKeyValue) {
            return Optional.empty()
        }
        val newStateValue = transformer.toStateValue(newKeyValue)

        if (newStateValue == currentStateValue) {
            return Optional.of(this as S)
        } else if ((property.getPredicate() as Predicate<Comparable<*>>).test(newStateValue)) {
            return Optional.empty()
        }

        return Optional.of(this.propertyValueTable.row(property)[newStateValue] as S)
    }

    override fun <E> with(key: Key<out Value<E>>, value: E): Optional<S> {
        val property = (this.keysToProperty[key] as? AbstractStateProperty<*, E>) ?: return Optional.empty()

        val stateValue = property.keyValueTransformer.toStateValue(value)
        val currentStateValue = this.stateValues[property]

        if (stateValue == currentStateValue) {
            return Optional.of(this as S)
        } else if ((property.getPredicate() as Predicate<Comparable<*>>).test(stateValue)) {
            return Optional.empty()
        }

        return Optional.of(this.propertyValueTable.row(property)[stateValue] as S)
    }

    override fun with(value: Value<*>) = with(value.key as Key<Value<Any>>, value.get())

    override fun with(valueContainer: ImmutableDataManipulator<*, *>): Optional<S> {
        var state: Optional<S>? = null
        for (value in valueContainer.getValues()) {
            state = with(value)
            if (!state.isPresent) {
                return state
            }
        }
        return state ?: Optional.of(this as S)
    }

    override fun with(valueContainers: Iterable<ImmutableDataManipulator<*, *>>): Optional<S> {
        var state: Optional<S>? = null
        for (valueContainer in valueContainers) {
            state = with(valueContainer)
            if (!state.isPresent) {
                return state
            }
        }
        return state ?: Optional.of(this as S)
    }

    override fun without(containerClass: Class<out ImmutableDataManipulator<*, *>>) = Optional.empty<S>()

    override fun merge(that: S): S {
        return if (this.stateContainer != (that as IState<S>).stateContainer) {
            this as S
        } else {
            var temp = this as S
            for (manipulator in that.manipulators) {
                val optional = temp.with(manipulator)
                if (optional.isPresent) {
                    temp = optional.get()
                }
            }
            temp
        }
    }

    override fun merge(that: S, function: MergeFunction): S {
        return if (this.stateContainer != (that as IState<S>).stateContainer) {
            this as S
        } else {
            var temp = this as S
            for (manipulator in that.manipulators) {
                val old = temp.get(manipulator.javaClass).orNull()
                val optional = temp.with(checkNotNull(function.merge(old, manipulator)))
                if (optional.isPresent) {
                    temp = optional.get()
                }
            }
            temp
        }
    }

    override fun <E> get(key: Key<out Value<E>>): Optional<E> {
        val property = (this.keysToProperty[key] as? AbstractStateProperty<*, E>) ?: return Optional.empty()
        val transformer = property.keyValueTransformer as StateKeyValueTransformer<Comparable<Any>, E>

        val currentStateValue = this.stateValues[property]
        val currentKeyValue = transformer.toKeyValue(currentStateValue as Comparable<Any>)

        return Optional.of(currentKeyValue)
    }

    override fun <E : Any, V : Value<E>> getRawMutableValueFor(key: Key<V>): Optional<V> {
        val property = (this.keysToProperty[key] as? AbstractStateProperty<*, E>) ?: return Optional.empty()
        val transformer = property.keyValueTransformer as StateKeyValueTransformer<Comparable<Any>, E>

        val currentStateValue = this.stateValues[property]
        val currentKeyValue = transformer.toKeyValue(currentStateValue as Comparable<Any>)

        return Optional.of(LanternMutableValue(key, currentKeyValue) as V)
    }

    override fun supports(key: Key<*>) = key in this.keysToProperty

    override fun copy(): S = this as S
    override fun getKeys() = (this.stateContainer as AbstractStateContainer<*>).keys
    override fun getValues() = this.values

    override fun toStringHelper() = super.toStringHelper()
}
