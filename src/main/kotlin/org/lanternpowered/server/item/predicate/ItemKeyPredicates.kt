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
            override fun test(stack: ItemStack) = keyValueMatcher.matcherContainer(stack)
            override fun test(stack: ItemStackSnapshot) = keyValueMatcher.matcherContainer(stack)
            override fun test(type: ItemType) = keyValueMatcher.matcherContainer(type)
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
            override fun test(stack: ItemStack) = stack.get(key).isPresent
            override fun test(stack: ItemStackSnapshot) = stack.get(key).isPresent
            override fun test(type: ItemType) = type.get(key).isPresent
        }
    }
}
