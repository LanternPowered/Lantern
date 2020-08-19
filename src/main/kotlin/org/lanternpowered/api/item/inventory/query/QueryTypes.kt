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
package org.lanternpowered.api.item.inventory.query

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ExtendedItemStack
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.provide
import org.spongepowered.math.vector.Vector2i

object QueryTypes {

    // SORTFIELDS:ON

    /**
     * Tests based on the class of the inventory.
     */
    val INVENTORY_TYPE: OneParamQueryType<Class<out Inventory>> by CatalogRegistry.provide("inventory_type")

    /**
     * Allows a custom condition for the items contained within an item stack.
     */
    val ITEM_STACK: OneParamQueryType<(ItemStackSnapshot) -> Boolean> by CatalogRegistry.provide("item_stack_filter")

    /**
     * Tests for an exact match of the item stack contained in each slot.
     *
     * Generally uses the [ExtendedItemStack.isEqualTo] method.
     */
    val EXACT_ITEM_STACK: OneParamQueryType<ItemStack> by CatalogRegistry.provide("item_stack_exact")

    /**
     * Tests for an similar match of the item stack contained in each slot.
     *
     * Generally uses the [ExtendedItemStack.isSimilarTo] method.
     */
    val SIMILAR_ITEM_STACK: OneParamQueryType<ItemStack> by CatalogRegistry.provide("item_stack_ignore_quantity")

    /**
     * Tests for a match of the type of item contained in each slot.
     *
     * @see ExtendedItemStack.getType
     */
    val ITEM_TYPE: OneParamQueryType<ItemType> by CatalogRegistry.provide("item_type")

    /**
     * Query for a modified order of slots in a player inventory where the
     * hotbar will be prioritized, if present.
     */
    val PRIORITY_HOTBAR: NoParamQueryType by CatalogRegistry.provide("priority_hotbar")

    /**
     * Query for a modified order of slots in a player inventory where the
     * selected hotbar slot will be prioritized first, followed by the hotbar, if present.
     */
    val PRIORITY_SELECTED_SLOT_AND_HOTBAR: NoParamQueryType by CatalogRegistry.provide("priority_hotbar")

    /**
     * Query for a reverse order of slots.
     */
    val REVERSE: NoParamQueryType by CatalogRegistry.provide("reverse")

    /**
     * A grid query. Only works on grids. The first value is the offset the second value is the grid size.
     */
    val GRID: TwoParamQueryType<Vector2i, Vector2i> by CatalogRegistry.provide("grid")
}
