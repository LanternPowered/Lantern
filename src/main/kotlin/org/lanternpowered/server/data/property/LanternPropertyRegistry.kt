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
package org.lanternpowered.server.data.property

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import org.lanternpowered.api.data.property.DoublePropertyProvider
import org.lanternpowered.api.data.property.IntPropertyProvider
import org.lanternpowered.api.data.property.Property
import org.lanternpowered.api.data.property.PropertyHolder
import org.lanternpowered.api.data.property.PropertyProvider
import org.lanternpowered.api.ext.*
import java.util.ArrayList
import java.util.Comparator
import java.util.concurrent.ConcurrentHashMap

open class LanternPropertyRegistry<H : PropertyHolder> : PropertyRegistry<H> {

    private val propertyProviders = HashMultimap.create<Property<*>, PropertyProvider<*>>()
    private val cachedDelegates = ConcurrentHashMap<Property<*>, PropertyProvider<*>>()

    private var cachedProviders: Map<Property<*>, PropertyProvider<*>>? = null

    override val providers: Map<Property<*>, PropertyProvider<*>>
        get() {
            var cachedProvidersMap = this.cachedProviders
            if (cachedProvidersMap != null) {
                return cachedProvidersMap
            }
            val builder = ImmutableMap.builder<Property<*>, PropertyProvider<*>>()
            for ((property, stores) in this.propertyProviders.entries()) {
                val delegate = this.cachedDelegates.computeIfAbsent(property) {
                    constructDelegate(property.uncheckedCast<Property<Any>>(), stores.uncheckedCast())
                }
                builder.put(property, delegate)
            }
            cachedProvidersMap = builder.build()
            this.cachedProviders = cachedProvidersMap
            return cachedProvidersMap
        }

    private fun <V : Any> constructDelegate(property: Property<V>): PropertyProvider<V> {
        return constructDelegate(property, this.propertyProviders[property].uncheckedCast())
    }

    protected open fun <V : Any> constructDelegate(property: Property<V>, propertyProviders: Collection<PropertyProvider<V>>): PropertyProvider<V> {
        if (propertyProviders.size == 1) {
            return propertyProviders.iterator().next()
        }
        val providers = ArrayList(propertyProviders)
        providers.sortWith(Comparator.comparing(PropertyProvider<V>::getPriority))
        val immutableProviders = ImmutableList.copyOf(providers)
        val valueType = property.valueType.rawType
        if (valueType == Int::class.java) {
            return IntPropertyProviderDelegate(immutableProviders.uncheckedCast()).uncheckedCast()
        } else if (valueType == Double::class.java) {
            return DoublePropertyProviderDelegate(immutableProviders.uncheckedCast()).uncheckedCast()
        }
        return PropertyProviderDelegate(immutableProviders)
    }

    override fun <V : Any> registerProvider(property: Property<V>, propertyProvider: PropertyProvider<V>) {
        this.propertyProviders.put(property, propertyProvider)
        this.cachedProviders = null
        this.cachedDelegates.remove(property)
    }

    override fun <V : Any> registerProvider(property: Property<V>, fn: PropertyProviderBuilder<V, H>.(property: Property<V>) -> Unit) {
        val builder = LanternPropertyProviderBaseBuilder<V, H>()
        builder.fn(property)
        registerProvider(property, builder.build())
    }

    override fun <V : Any> register(property: Property<V>, constant: V) {
        val store = when (property.valueType.rawType) {
            Int::class.java -> ConstantIntPropertyProvider(constant.uncheckedCast()).uncheckedCast()
            Double::class.java -> ConstantDoublePropertyProvider(constant.uncheckedCast()).uncheckedCast()
            else -> ConstantPropertyProvider(constant)
        }
        registerProvider(property, store)
    }

    override fun <V : Any> getProvider(property: Property<V>): PropertyProvider<V> {
        return this.cachedDelegates.computeIfAbsent(property) { constructDelegate(property) }.uncheckedCast()
    }

    override fun getIntProvider(property: Property<Int>) = getProvider(property) as IntPropertyProvider
    override fun getDoubleProvider(property: Property<Double>) = getProvider(property) as DoublePropertyProvider
}
