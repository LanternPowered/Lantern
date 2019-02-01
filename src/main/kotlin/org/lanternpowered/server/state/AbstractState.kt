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

import org.lanternpowered.server.catalog.AbstractCatalogType
import org.lanternpowered.server.util.ToStringHelper
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.Property
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator
import org.spongepowered.api.data.merge.MergeFunction
import org.spongepowered.api.data.value.BaseValue
import org.spongepowered.api.data.value.immutable.ImmutableValue
import org.spongepowered.api.state.State
import org.spongepowered.api.state.StateProperty
import java.util.Optional
import java.util.function.Function

abstract class AbstractState<S : State<S>> : AbstractCatalogType(), State<S> {

    override fun <T : Comparable<T>> getPropertyValue(blockTrait: StateProperty<T>): Optional<T> {
        return Optional.empty()
    }

    override fun getProperty(blockTrait: String): Optional<StateProperty<*>> {
        return Optional.empty()
    }

    override fun <T : Comparable<T>, V : T> withProperty(trait: StateProperty<*>, value: V): Optional<S> {
        return Optional.empty()
    }

    override fun <T : Comparable<T>> cycleProperty(property: StateProperty<T>): Optional<S> {
        return Optional.empty()
    }

    override fun getProperties(): Collection<StateProperty<*>> {
        return null
    }

    override fun getPropertyValues(): Collection<*> {
        return null
    }

    override fun getPropertyMap(): Map<StateProperty<*>, *> {
        return null
    }

    override fun getKey(): CatalogKey {
        return null
    }

    override fun getManipulators(): List<ImmutableDataManipulator<*, *>> {
        return null
    }

    override fun getContentVersion(): Int {
        return 0
    }

    override fun toContainer(): DataContainer {
        return null
    }

    override fun <T : Property<*, *>> getProperty(propertyClass: Class<T>): Optional<T> {
        return Optional.empty()
    }

    override fun getApplicableProperties(): Collection<Property<*, *>> {
        return null
    }

    override fun <T : ImmutableDataManipulator<*, *>> get(containerClass: Class<T>): Optional<T> {
        return Optional.empty()
    }

    override fun <T : ImmutableDataManipulator<*, *>> getOrCreate(containerClass: Class<T>): Optional<T> {
        return Optional.empty()
    }

    override fun supports(containerClass: Class<out ImmutableDataManipulator<*, *>>): Boolean {
        return false
    }

    override fun <E> transform(key: Key<out BaseValue<E>>, function: Function<E, E>): Optional<S> {
        return Optional.empty()
    }

    override fun <E> with(key: Key<out BaseValue<E>>, value: E): Optional<S> {
        return Optional.empty()
    }

    override fun with(value: BaseValue<*>): Optional<S> {
        return Optional.empty()
    }

    override fun with(valueContainer: ImmutableDataManipulator<*, *>): Optional<S> {
        return Optional.empty()
    }

    override fun with(valueContainers: Iterable<ImmutableDataManipulator<*, *>>): Optional<S> {
        return Optional.empty()
    }

    override fun without(containerClass: Class<out ImmutableDataManipulator<*, *>>): Optional<S> {
        return Optional.empty()
    }

    override fun merge(that: S): S {
        return null
    }

    override fun merge(that: S, function: MergeFunction): S {
        return null
    }

    override fun getContainers(): List<ImmutableDataManipulator<*, *>> {
        return null
    }

    override fun <E> get(key: Key<out BaseValue<E>>): Optional<E> {
        return Optional.empty()
    }

    override fun <E, V : BaseValue<E>> getValue(key: Key<V>): Optional<V> {
        return Optional.empty()
    }

    override fun supports(key: Key<*>): Boolean {
        return false
    }

    override fun copy(): S {
        return null
    }

    override fun getKeys(): Set<Key<*>> {
        return null
    }

    override fun getValues(): Set<ImmutableValue<*>> {
        return null
    }

    override fun toStringHelper() = super.toStringHelper().apply {

    }
}
