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

import org.lanternpowered.api.data.Key
import org.spongepowered.api.data.KeyValueMatcher
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot

object ItemKeyPredicates {

    /**
     * Constructs a [ItemPredicate] for the given [KeyValueMatcher].
     *
     * @param keyValueMatcher The key value matcher
     * @return The item filter
     */
    fun hasMatchingKey(keyValueMatcher: KeyValueMatcher<*>): ItemPredicate {
        return object : ItemPredicate {
            override fun invoke(stack: ItemStack) = keyValueMatcher.matchesContainer(stack)
            override fun invoke(stack: ItemStackSnapshot) = keyValueMatcher.matchesContainer(stack)
            override fun invoke(type: ItemType) = keyValueMatcher.matchesContainer(type)
        }
    }

    /**
     * Constructs a [ItemPredicate] that matches whether
     * the [Key] is present on the [ItemStack].
     *
     * @param key The key
     * @return The item filter
     */
    fun <T> hasKey(key: Key<out Value<T>>): ItemPredicate {
        return object : ItemPredicate {
            override fun invoke(stack: ItemStack) = stack.get(key).isPresent
            override fun invoke(stack: ItemStackSnapshot) = stack.get(key).isPresent
            override fun invoke(type: ItemType) = type.get(key).isPresent
        }
    }
}
