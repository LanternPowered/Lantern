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

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.reflect.TypeToken
import org.lanternpowered.api.util.Named
import org.lanternpowered.server.block.BlockProperties
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.inventory.LanternInventoryProperties
import org.lanternpowered.server.item.ItemProperties
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.CatalogType
import org.spongepowered.api.data.property.Properties
import org.spongepowered.api.data.property.Property
import org.spongepowered.api.data.property.PropertyHolder
import org.spongepowered.api.data.property.PropertyRegistry
import org.spongepowered.api.data.property.store.DoublePropertyStore
import org.spongepowered.api.data.property.store.IntPropertyStore
import org.spongepowered.api.data.property.store.PropertyStore
import org.spongepowered.api.item.inventory.InventoryProperties
import java.lang.reflect.Modifier
import java.util.ArrayList
import java.util.Comparator
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object LanternPropertyRegistry : AdditionalPluginCatalogRegistryModule<Property<*>>(), PropertyRegistry {

    private val propertyStoreMap = HashMultimap.create<Property<*>, PropertyStore<*>>()
    private val delegateMap = ConcurrentHashMap<Property<*>, PropertyStoreDelegate<*>>()

    // A delegate map specialized to be used in inventories
    private val inventoryDelegateMap = ConcurrentHashMap<Property<*>, PropertyStoreDelegate<*>>()

    override fun registerDefaults() {
        val typeVariable = Property::class.java.typeParameters[0]

        fun registerCatalog(catalog: KClass<*>, inventoryProperties: Boolean = false) {
            for (field in catalog.java.declaredFields) {
                if (!Modifier.isStatic(field.modifiers)) {
                    continue
                }

                val name = field.getAnnotation(Named::class.java)?.value ?: field.name

                val key = CatalogKey.sponge(name.toLowerCase())
                val optProperty = get(key)

                val property: Property<*>
                if (optProperty.isPresent) {
                    property = optProperty.get()
                } else {
                    val typeToken = TypeToken.of(field.genericType)
                    val valueType = typeToken.resolveType(typeVariable)
                    property = LanternPropertyBuilder<CatalogType>()
                            .valueType(valueType)
                            .key(key)
                            .build()
                    registerAdditionalCatalog(property)
                }
                if (inventoryProperties) {
                    register(property as Property<Any>, InventoryPropertyStore(property))
                }
                try {
                    field.set(null, property)
                } catch (e: IllegalAccessException) {
                    throw IllegalStateException(e)
                }
            }
        }

        registerCatalog(Properties::class)
        registerCatalog(InventoryProperties::class, inventoryProperties = true)
        registerCatalog(LanternInventoryProperties::class, inventoryProperties = true)
        registerCatalog(ItemProperties::class)
        registerCatalog(BlockProperties::class)
    }

    fun completeRegistration() {
        finalizeContent()
        for ((key, value) in this.propertyStoreMap.asMap()) {
            this.delegateMap[key] = constructDelegate(key as Property<Any>, value as Collection<PropertyStore<Any>>)
        }
        this.propertyStoreMap.clear()
    }

    private fun <V> constructDelegate(property: Property<V>): PropertyStoreDelegate<V> {
        return constructDelegate(property, this.propertyStoreMap[property] as Collection<PropertyStore<V>>)
    }

    private fun <V> constructDelegate(property: Property<V>, propertyStores: Collection<PropertyStore<V>>): PropertyStoreDelegate<V> {
        val stores = ArrayList(propertyStores)
        stores.sortWith(Comparator.comparing(PropertyStore<V>::getPriority))
        val immutableStores = ImmutableList.copyOf(stores)
        val valueType = property.valueType.rawType
        if (valueType == Int::class.java) {
            immutableStores as ImmutableList<PropertyStore<Int>>
            return IntPropertyStoreDelegate(immutableStores) as PropertyStoreDelegate<V>
        } else if (valueType == Double::class.java) {
            immutableStores as ImmutableList<PropertyStore<Double>>
            return DoublePropertyStoreDelegate(immutableStores) as PropertyStoreDelegate<V>
        }
        return PropertyStoreDelegate(immutableStores)
    }

    override fun <V> register(property: Property<V>, store: PropertyStore<V>) {
        checkFinalizedContent()
        this.propertyStoreMap.put(property, store)
    }

    override fun <V> getStore(property: Property<V>): PropertyStore<V> {
        checkNotNull(property, "property")
        if (isContentFinalized) {
            return constructDelegate(property)
        }
        return this.delegateMap.computeIfAbsent(property) { constructDelegate(property) } as PropertyStore<V>
    }

    fun <V> getStoreForInventory(property: Property<V>): PropertyStore<V> {
        checkNotNull(property, "property")
        if (isContentFinalized) {
            return constructInventoryDelegate(property)
        }
        return this.inventoryDelegateMap.computeIfAbsent(property) { constructInventoryDelegate(property) } as PropertyStore<V>
    }

    private fun <V> constructInventoryDelegate(property: Property<V>): PropertyStoreDelegate<V> {
        // Filter out inventory stores to avoid infinite loops
        // when a delegate store is called from a inventory
        val propertyStores = (this.propertyStoreMap[property] as Collection<PropertyStore<V>>).stream()
                .filter { it !is InventoryPropertyStore }
                .collect(ImmutableList.toImmutableList())
        return constructDelegate(property, propertyStores)
    }

    override fun getIntStore(property: Property<Int>) = getStore(property) as IntPropertyStore
    override fun getDoubleStore(property: Property<Double>) = getStore(property) as DoublePropertyStore

    fun getPropertiesFor(holder: PropertyHolder): Map<Property<*>, *> {
        val builder = ImmutableMap.builder<Property<*>, Any>()
        for ((key, store) in this.delegateMap) {
            store.getFor(holder).ifPresent { value -> builder.put(key, value!!) }
        }
        return builder.build()
    }
}
