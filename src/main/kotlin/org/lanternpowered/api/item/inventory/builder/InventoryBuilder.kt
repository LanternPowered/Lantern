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
package org.lanternpowered.api.item.inventory.builder

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.archetype.InventoryArchetype
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.archetype.SlotArchetype
import org.lanternpowered.api.item.inventory.slot.EquipmentSlot
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.slot.FilteringSlot
import org.lanternpowered.api.item.inventory.slot.FuelSlot
import org.lanternpowered.api.item.inventory.slot.InputSlot
import org.lanternpowered.api.item.inventory.slot.OutputSlot
import org.spongepowered.api.item.inventory.equipment.EquipmentGroup
import org.spongepowered.api.item.inventory.equipment.EquipmentType
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes
import java.util.function.Supplier

fun inventoryArchetype(fn: InventoryBuilder<InventoryArchetype<*>, SlotArchetype<*>>.() -> Unit): InventoryArchetype<ExtendedInventory> = TODO()

fun slotArchetype(fn: SlotBuilder<SlotArchetype<*>>.() -> Unit): SlotArchetype<ExtendedSlot> = TODO()

fun inventory(fn: InventoryBuilder<Inventory, Slot>.() -> Unit): Inventory = TODO()

interface InventoryBuilder<I, S> {

    /**
     * Applies a "tag" to the inventory, which can be queried for later.
     */
    fun tag(tag: String)

    /**
     * Adds the given child inventory.
     */
    fun child(inventory: I)

    fun slot(fn: SlotBuilder<I>.() -> Unit)

    fun slot()

    fun column(height: Int, fn: ColumnBuilder<I, S>.(y: Int) -> Unit)

    fun column(fn: ColumnBuilder<I, S>.(y: Int) -> Unit)

    fun row(width: Int, fn: RowBuilder<I, S>.(x: Int) -> Unit)

    fun row(fn: RowBuilder<I, S>.(x: Int) -> Unit)

    fun grid(width: Int, height: Int, fn: GridBuilder<I, S>.() -> Unit)

    fun grid(width: Int, height: Int)

    fun grid(fn: GridBuilder<I, S>.() -> Unit)
}

interface GridBuilder<T, S> {

    fun slot(slot: S)

    fun slotAt(x: Int, y: Int, slot: S)

    fun slots(fn: SlotBuilder<S>.(x: Int, y: Int) -> Unit)

    fun slots(fn: SlotBuilder<S>.() -> Unit) = this.slots { _, _ -> fn() }

    fun slotAt(x: Int, y: Int, fn: SlotBuilder<S>.() -> Unit)

    fun columnAt(x: Int, fn: ColumnBuilder<T, S>.(y: Int) -> Unit)

    fun column(height: Int, fn: ColumnBuilder<T, S>.(y: Int) -> Unit)

    fun column(height: Int)

    fun rowAt(y: Int, fn: RowBuilder<T, S>.(x: Int) -> Unit)

    fun row(width: Int, fn: RowBuilder<T, S>.(x: Int) -> Unit)

    fun row(width: Int)

    fun grid(width: Int, height: Int, fn: GridBuilder<T, S>.() -> Unit)

    fun grid(width: Int, height: Int)

    fun grid(fn: GridBuilder<T, S>.() -> Unit)
}

interface ColumnBuilder<T, S> {

    fun slot(slot: S)

    fun slotAt(y: Int, slot: S)

    fun slots(fn: SlotBuilder<S>.(y: Int) -> Unit)

    fun slots(fn: SlotBuilder<S>.() -> Unit) = this.slots { _ -> fn() }

    fun slotsAt(yRange: IntRange, fn: SlotBuilder<S>.(y: Int) -> Unit)

    fun slotsAt(yRange: IntRange, fn: SlotBuilder<S>.() -> Unit) = this.slotsAt(yRange) { _ -> fn() }

    fun slotAt(y: Int, fn: SlotBuilder<S>.() -> Unit)

    fun slot(fn: SlotBuilder<S>.() -> Unit)
}

interface RowBuilder<T, S> {

    fun slot(slot: S)

    fun slotAt(x: Int, slot: S)

    fun slots(fn: SlotBuilder<S>.(x: Int) -> Unit)

    fun slots(fn: SlotBuilder<S>.() -> Unit) = this.slots { _ -> fn() }

    fun slotsAt(xRange: IntRange, fn: SlotBuilder<S>.(x: Int) -> Unit)

    fun slotsAt(xRange: IntRange, fn: SlotBuilder<S>.() -> Unit) = this.slotsAt(xRange) { _ -> fn() }

    fun slotAt(x: Int, fn: SlotBuilder<S>.() -> Unit)

    fun slot(fn: SlotBuilder<S>.() -> Unit)
}

/**
 * This applies an equipment filter to the slot, this means that only
 * certain items will be accepted by the slot. This will turn the slot
 * in an [EquipmentSlot].
 */
fun SlotBuilder<*>.equipment(type: Supplier<out EquipmentType>) =
        this.equipment(type.get())

/**
 * This applies an equipment filter to the slot, this means that only
 * certain items will be accepted by the slot. This will turn the slot
 * in an [EquipmentSlot].
 */
@JvmName("equipmentGroup")
fun SlotBuilder<*>.equipment(type: Supplier<out EquipmentGroup>) =
        this.equipment(type.get())

interface SlotBuilder<T> {

    /**
     * This applies a filter to the slot, this means that only certain
     * items will be accepted by the slot. This will turn the slot in
     * a [FilteringSlot].
     */
    fun filter(fn: (ItemStackSnapshot) -> Boolean)

    /**
     * This applies an equipment filter to the slot, this means that only
     * certain items will be accepted by the slot. This will turn the slot
     * in an [EquipmentSlot].
     */
    fun equipment()

    /**
     * This applies an equipment filter to the slot, this means that only
     * certain items will be accepted by the slot. This will turn the slot
     * in an [EquipmentSlot].
     */
    fun equipment(type: EquipmentType)

    /**
     * This applies an equipment filter to the slot, this means that only
     * certain items will be accepted by the slot. This will turn the slot
     * in an [EquipmentSlot].
     */
    fun equipment(type: EquipmentGroup)

    /**
     * Marks the slot as a [FuelSlot]. This does not apply any filters.
     */
    fun fuel()

    /**
     * Marks the slot as an [InputSlot]. This does not apply any filters.
     */
    fun input()

    /**
     * Marks the slot as an [OutputSlot]. This does not apply any filters.
     */
    fun output()
}

fun test() {
    inventoryArchetype {
        // Equipment
        column {
            slot {
                equipment(EquipmentTypes.HEAD)
            }
            slot {
                equipment(EquipmentTypes.CHEST)
            }
            slot {
                equipment(EquipmentTypes.LEGS)
            }
            slot {
                equipment(EquipmentTypes.FEET)
            }
        }
        // Primary inventory
        grid {
            // Storage
            grid(9, 3)
            // Hot bar
            row(9)
        }
        // Off hand
        slot {
            equipment(EquipmentTypes.OFF_HAND)
        }
    }
}
