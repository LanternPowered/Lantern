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
package org.lanternpowered.server.item.predicate

import org.spongepowered.api.data.property.Property
import org.spongepowered.api.data.property.PropertyMatcher
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot

object PropertyItemPredicates {

    /**
     * Constructs a [ItemPredicate] for the given [PropertyMatcher].
     *
     * @param propertyMatcher The property matcher
     * @return The item filter
     */
    fun hasMatchingProperty(propertyMatcher: PropertyMatcher<*>): ItemPredicate {
        return object : ItemPredicate {
            override fun test(stack: ItemStack) = propertyMatcher.matchesHolder(stack)
            override fun test(stack: ItemStackSnapshot) = propertyMatcher.matchesHolder(stack)
            override fun test(type: ItemType) = propertyMatcher.matchesHolder(type)
        }
    }

    /**
     * Constructs a [ItemPredicate] that matches whether
     * the [Property] is present on the [ItemStack].
     *
     * @param property The property
     * @return The item filter
     */
    fun hasProperty(property: Property<*>): ItemPredicate {
        return object : ItemPredicate {
            override fun test(stack: ItemStack) = stack.getProperty(property).isPresent
            override fun test(stack: ItemStackSnapshot) = stack.getProperty(property).isPresent
            override fun test(type: ItemType) = type.getProperty(property).isPresent
        }
    }
}
