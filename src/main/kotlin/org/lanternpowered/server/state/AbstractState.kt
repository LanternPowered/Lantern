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
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.catalog.AbstractCatalogType
import org.lanternpowered.server.data.SerializableImmutableDataHolder
import org.lanternpowered.server.state.property.AbstractStateProperty
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateContainer
import org.spongepowered.api.state.StateProperty
import org.spongepowered.api.util.Cycleable
import java.util.Optional
import java.util.function.Function
import java.util.function.Predicate

@Suppress("UNCHECKED_CAST")
abstract class AbstractState<S : State<S>, C : StateContainer<S>>(builder: StateBuilder<S>) : AbstractCatalogType(),
        IState<S>, SerializableImmutableDataHolder<S> {

    private val key: CatalogKey

    // The container this state is linked to
    final override val stateContainer: C

    // All the values of this state
    private val stateValues: ImmutableMap<StateProperty<*>, Comparable<*>>

    // A lookup table to get a specific state when you would change a value
    internal lateinit var propertyValueTable: ImmutableTable<StateProperty<*>, Comparable<*>, S>

    // The internal id of this state within the state container
    final override val internalId: Int

    // A list with all the values of this state
    private val values: ImmutableSet<Value.Immutable<*>>

    // Serialized data container
    private val dataContainer: DataContainer

    init {
        builder as LanternStateBuilder<S>

        this.key = builder.key
        this.stateValues = builder.stateValues
        this.stateContainer = builder.stateContainer as C
        this.internalId = builder.internalId
        this.dataContainer = builder.dataContainer
        this.values = builder.values
    }

    override fun <T : Comparable<T>> getStateProperty(stateProperty: StateProperty<T>): Optional<T> {
        return (this.stateValues[stateProperty] as? T).optional()
    }

    override fun <T : Comparable<T>, V : T> withStateProperty(stateProperty: StateProperty<T>, value: V): Optional<S> {
        return this.propertyValueTable.row(stateProperty)?.get(value).optional()
    }

    override fun getStatePropertyByName(statePropertyId: String): Optional<StateProperty<*>> {
        for ((property, _) in this.stateValues) {
            if (property.getName() == statePropertyId) {
                return property.optional()
            }
        }
        return emptyOptional()
    }

    override fun supportsStateProperty(stateProperty: StateProperty<*>): Boolean
            = stateProperty in this.stateValues

    override fun <T : Comparable<T>> cycleStateProperty(stateProperty: StateProperty<T>): Optional<S> {
        var value = this.stateValues[stateProperty] as? T ?: return emptyOptional()

        if (value is Cycleable<*>) {
            var last = value
            var next: T
            while (true) {
                next = (last as Cycleable<*>).cycleNext() as T
                if (next === value) {
                    // We cycled completely, abort
                    return (this as S).optional()
                }
                // Check if the next value is actually supported
                if (stateProperty.predicate.test(next)) {
                    value = next
                    break
                }
                last = next
            }
        } else {
            val list = (stateProperty as IStateProperty<T,*>).sortedPossibleValues
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

        return (this.propertyValueTable.row(stateProperty)[value] as S).optional()
    }

    override fun <T : Cycleable<T>> cycleValue(key: Key<out Value<T>>): Optional<S> {
        val value = get(key).orNull() ?: return emptyOptional()

        var last = value
        var next: T
        while (true) {
            next = (last as Cycleable<*>).cycleNext() as T
            if (next === value) {
                // We cycled completely, abort
                return (this as S).optional()
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

    override fun <E : Any> transform(key: Key<out Value<E>>, function: Function<E, E>): Optional<S> {
        val keysToProperty = (this.stateContainer as AbstractStateContainer<*>).keysToProperty

        val property = (keysToProperty[key] as? AbstractStateProperty<*, E>)
                ?: return super<SerializableImmutableDataHolder>.transform(key, function)
        val transformer = property.keyValueTransformer as StateKeyValueTransformer<Comparable<Any>, E>

        val currentStateValue = this.stateValues[property]
        val currentKeyValue = transformer.toKeyValue(currentStateValue as Comparable<Any>)

        val newKeyValue = function.apply(currentKeyValue)
        if (newKeyValue === currentKeyValue) {
            return emptyOptional()
        }
        val newStateValue = transformer.toStateValue(newKeyValue)

        return withValue(property, currentStateValue, newStateValue)
    }

    override fun <E : Any> with(key: Key<out Value<E>>, value: E): Optional<S> {
        val keysToProperty = (this.stateContainer as AbstractStateContainer<*>).keysToProperty
        val property = (keysToProperty[key] as? AbstractStateProperty<*, E>)
                ?: return super<SerializableImmutableDataHolder>.with(key, value)

        val stateValue = property.keyValueTransformer.toStateValue(value)
        val currentStateValue = this.stateValues[property]

        return withValue(property, currentStateValue, stateValue)
    }

    private fun withValue(
            property: AbstractStateProperty<*, *>, currentStateValue: Comparable<*>?, stateValue: Comparable<*>
    ): Optional<S> {
        if (stateValue == currentStateValue) {
            return (this as S).optional()
        } else if ((property.getPredicate() as Predicate<Comparable<*>>).test(stateValue)) {
            return emptyOptional()
        }
        return (this.propertyValueTable.row(property)[stateValue] as S).optional()
    }

    override fun with(value: Value<*>) = with(value.key as Key<Value<Any>>, value.get())

    override fun without(key: Key<*>): Optional<S> {
        val keysToProperty = (this.stateContainer as AbstractStateContainer<*>).keysToProperty
        return if (key in keysToProperty) emptyOptional() else super<SerializableImmutableDataHolder>.without(key)
    }

    override fun mergeWith(that: S, function: MergeFunction): S {
        if (this.stateContainer != (that as IState<S>).stateContainer) {
            return this as S
        }
        return super<SerializableImmutableDataHolder>.mergeWith(that, function)
    }

    override fun <E : Any> get(key: Key<out Value<E>>): Optional<E> {
        val keysToProperty = (this.stateContainer as AbstractStateContainer<*>).keysToProperty

        val property = (keysToProperty[key] as? AbstractStateProperty<*, E>)
                ?: return super<SerializableImmutableDataHolder>.get(key)
        val transformer = property.keyValueTransformer as StateKeyValueTransformer<Comparable<Any>, E>

        val currentStateValue = this.stateValues[property]
        val currentKeyValue = transformer.toKeyValue(currentStateValue as Comparable<Any>)

        return currentKeyValue.optional()
    }

    override fun supports(key: Key<*>): Boolean {
        val keysToProperty = (this.stateContainer as AbstractStateContainer<*>).keysToProperty
        return key in keysToProperty || super<SerializableImmutableDataHolder>.supports(key)
    }

    override fun copy(): S = this as S

    override fun getKeys() = (this.stateContainer as AbstractStateContainer<*>).keysToProperty.keys

    override fun getValues(): Set<Value.Immutable<*>> {
        val values = super.getValues()
        if (values.isEmpty()) return this.values
        return ImmutableSet.builderWithExpectedSize<Value.Immutable<*>>(values.size + this.values.size)
                .addAll(values)
                .addAll(this.values)
                .build()
    }

    override fun toStringHelper() = super.toStringHelper()
}
