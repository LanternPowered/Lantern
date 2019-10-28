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

import com.google.common.collect.ImmutableList
import com.google.common.reflect.TypeToken
import org.lanternpowered.api.data.property.Property
import org.lanternpowered.api.data.property.PropertyHolder
import org.lanternpowered.api.data.property.PropertyProvider
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.Named
import org.lanternpowered.server.block.BlockProperties
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.lanternpowered.server.inventory.LanternInventoryProperties
import org.lanternpowered.server.item.ItemProperties
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.CatalogType
import org.spongepowered.api.data.property.Properties
import org.spongepowered.api.item.inventory.InventoryProperties
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule
import java.lang.reflect.Modifier
import java.util.ArrayList
import java.util.Comparator
import kotlin.reflect.KClass

object LanternGlobalPropertyRegistry : LanternPropertyRegistry<PropertyHolder>(),
        AdditionalCatalogRegistryModule<Property<*>> by AdditionalPluginCatalogRegistryModule<Property<*>>() {

    override fun <V : Any> constructDelegate(
            property: Property<V>, propertyProviders: Collection<PropertyProvider<V>>): PropertyProvider<V> {
        val stores = ArrayList(propertyProviders)
        stores.sortWith(Comparator.comparing(PropertyProvider<V>::getPriority))
        val immutableStores = ImmutableList.copyOf(stores)
        val valueType = property.valueType.rawType
        if (valueType == Int::class.java) {
            return GlobalIntPropertyProviderDelegate(
                    property.uncheckedCast(), immutableStores.uncheckedCast()).uncheckedCast()
        } else if (valueType == Double::class.java) {
            return GlobalDoublePropertyProviderDelegate(
                    property.uncheckedCast(), immutableStores.uncheckedCast()).uncheckedCast()
        }
        return GlobalPropertyProviderDelegate(property.uncheckedCast(), immutableStores)
    }

    override fun registerDefaults() {
        val typeVariable = Property::class.java.typeParameters[0]

        fun registerCatalog(catalog: KClass<*>) {
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
                try {
                    field.set(null, property)
                } catch (e: IllegalAccessException) {
                    throw IllegalStateException(e)
                }
            }
        }

        registerCatalog(Properties::class)
        registerCatalog(InventoryProperties::class)
        registerCatalog(LanternInventoryProperties::class)
        registerCatalog(ItemProperties::class)
        registerCatalog(BlockProperties::class)
    }
}
