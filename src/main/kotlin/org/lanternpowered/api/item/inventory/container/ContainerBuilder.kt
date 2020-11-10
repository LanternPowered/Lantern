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
package org.lanternpowered.api.item.inventory.container

import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.ItemTypes
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.behavior.ShiftClickBehavior
import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import org.lanternpowered.api.item.inventory.entity.PlayerInventory
import org.lanternpowered.api.text.textOf

/**
 * Constructs a new container.
 */
fun container(fn: ContainerBuilder.() -> Unit): Container {
    TODO()
}

/**
 * A builder to construct [ExtendedContainer]s.
 */
interface ContainerBuilder {

    /**
     * Applies the layout of the container.
     */
    fun <L : ContainerLayout> layout(type: ExtendedContainerType<L>): L

    /**
     * Applies the layout of the container.
     */
    fun <L : ContainerLayout> layout(type: ExtendedContainerType<L>, fn: L.() -> Unit): L =
            this.layout(type).apply(fn)

    fun shiftClickBehavior(behavior: ShiftClickBehavior)
}

fun testFurnace(player: Player, playerInventory: PlayerInventory, furnaceInventory: Inventory) {
    val container = container {
        layout(ContainerTypes.Furnace) {
            top.slots(furnaceInventory)
            bottom.slots(playerInventory.primary)
        }
    }
    player.openInventory(container)
}

fun test(player: Player, playerInventory: PlayerInventory, furnaceInventory: Inventory) {
    val container = container {
        layout(ContainerTypes.Generic9x5) {
            title = textOf("My Fancy Furnace")
            top {
                fill(ContainerFills.Black)
                val slots = furnaceInventory.slots()
                get(1, 1).slot(slots[0]) // Input
                get(1, 2).button {
                    icon(ItemTypes.BLAZE_POWDER)
                }
                get(1, 3).slot(slots[1]) // Fuel
                get(3, 2).slot(slots[2]) // Output
            }
            bottom {
                slots(playerInventory.primary)
            }
        }
    }
    player.openInventory(container)
}
