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
package org.lanternpowered.server.inventory.query

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.EmptyInventory
import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.Inventory2D
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.hotbar.Hotbar
import org.lanternpowered.api.item.inventory.query
import org.lanternpowered.api.key.spongeKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.inventory.AbstractInventory
import org.lanternpowered.server.inventory.LanternChildrenInventory
import org.lanternpowered.server.inventory.entity.hotbar.SelectedHotbarSlotView
import org.spongepowered.api.data.KeyValueMatcher
import org.spongepowered.api.item.inventory.entity.PrimaryPlayerInventory
import org.spongepowered.api.item.inventory.query.QueryType
import org.spongepowered.math.vector.Vector2i
import java.util.function.Predicate

val QueryTypeRegistry = catalogTypeRegistry<QueryType> {
    fun register(id: String, fn: (inventory: ExtendedInventory) -> Inventory) =
            this.register(LanternNoParamQueryType(spongeKey(id), fn))

    fun <T> registerOneParam(id: String, fn: (inventory: ExtendedInventory, param: T) -> Inventory) =
            this.register(LanternOneParamQueryType(spongeKey(id), fn))

    fun <T1, T2> registerTwoParam(id: String, fn: (inventory: ExtendedInventory, param1: T1, param2: T2) -> Inventory) =
            this.register(LanternTwoParamQueryType(spongeKey(id), fn))

    registerOneParam<Class<out Inventory>>("inventory_type") { inventory, type ->
        inventory as AbstractInventory
        inventory.query(type.kotlin).firstOrNull() ?: inventory.empty()
    }
    registerOneParam<Class<Any>>("type") { inventory, type ->
        inventory as AbstractInventory
        inventory.queryAny(type.kotlin).firstOrNull() as? Inventory ?: inventory.empty()
    }
    registerOneParam<Predicate<ItemStack>>("item_stack_custom") { inventory, filter ->
        inventory as AbstractInventory
        val slots = inventory.slots()
                .filter { slot -> slot.contains { snapshot -> filter.test(snapshot.createStack()) } }
        LanternChildrenInventory(slots)
    }
    registerOneParam<ItemStack>("item_stack_exact") { inventory, stack ->
        inventory as AbstractInventory
        val slots = inventory.slots()
                .filter { slot -> slot.contains(stack) }
        LanternChildrenInventory(slots)
    }
    registerOneParam<ItemStack>("item_stack_ignore_quantity") { inventory, stack ->
        inventory as AbstractInventory
        val slots = inventory.slots()
                .filter { slot -> slot.containsAny(stack) }
        LanternChildrenInventory(slots)
    }
    registerOneParam<ItemType>("item_type") { inventory, type ->
        inventory as AbstractInventory
        val slots = inventory.slots()
                .filter { slot -> slot.contains(type) }
        LanternChildrenInventory(slots)
    }
    registerOneParam<KeyValueMatcher<*>>("key_value") { inventory, matcher ->
        inventory as AbstractInventory
        @Suppress("UNCHECKED_CAST")
        matcher as KeyValueMatcher<Any>
        val inventories = inventory
                .queryAny { inv -> matcher.matchesContainer(inv) }
                .toList()
        LanternChildrenInventory(inventories)
    }
    register("player_hotbar_primary_first") { inventory ->
        inventory as AbstractInventory
        val playerInventory = inventory.query<PrimaryPlayerInventory>().firstOrNull()
                ?: return@register inventory.empty()
        LanternChildrenInventory(listOf(
                playerInventory.hotbar as AbstractInventory,
                playerInventory as AbstractInventory))
    }
    register("reverse") { inventory ->
        inventory as AbstractInventory
        if (inventory is EmptyInventory)
            return@register inventory
        LanternChildrenInventory(inventory.slots().reversed())
    }
    registerTwoParam<Vector2i, Vector2i>("grid") { inventory, offset, size ->
        inventory as AbstractInventory
        if (inventory !is Inventory2D)
            return@registerTwoParam inventory.empty()
        TODO()
    }
    // Lantern
    registerOneParam<(ItemStackSnapshot) -> Boolean>("item_stack_filter") { inventory, filter ->
        inventory as AbstractInventory
        val slots = inventory.slots()
                .filter { slot -> slot.contains(filter) }
        LanternChildrenInventory(slots)
    }
    register("priority_hotbar") { inventory ->
        inventory as AbstractInventory
        if (inventory is Hotbar)
            return@register inventory
        val hotbar = inventory.query<Hotbar>().firstOrNull()
                ?: return@register inventory.empty()
        LanternChildrenInventory(listOf(hotbar as AbstractInventory, inventory))
    }
    register("priority_selected_slot_and_hotbar") { inventory ->
        inventory as AbstractInventory
        val hotbar = inventory.query<Hotbar>().firstOrNull()
                ?: return@register inventory.empty()
        hotbar as AbstractInventory
        val selectedSlot = SelectedHotbarSlotView(hotbar)
        LanternChildrenInventory(listOf(selectedSlot, hotbar, inventory))
    }
}
