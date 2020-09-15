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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.item.inventory

import org.lanternpowered.api.item.inventory.slot.EquipmentSlot
import org.lanternpowered.api.item.inventory.slot.ExtendedEquipmentSlot
import org.lanternpowered.api.item.inventory.slot.ExtendedFilteringSlot
import org.lanternpowered.api.item.inventory.slot.ExtendedFuelSlot
import org.lanternpowered.api.item.inventory.slot.ExtendedInputSlot
import org.lanternpowered.api.item.inventory.slot.ExtendedOutputSlot
import org.lanternpowered.api.item.inventory.slot.ExtendedSidedSlot
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.slot.FilteringSlot
import org.lanternpowered.api.item.inventory.slot.FuelSlot
import org.lanternpowered.api.item.inventory.slot.InputSlot
import org.lanternpowered.api.item.inventory.slot.OutputSlot
import org.lanternpowered.api.item.inventory.slot.SidedSlot
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.util.uncheckedCast
import kotlin.contracts.contract

/**
 * Gets the normal slots as an extended slots.
 */
inline fun List<Slot>.fix(): List<ExtendedSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slots as an extended slots.
 */
inline fun Iterable<Slot>.fix(): Iterable<ExtendedSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slots as an extended slots.
 */
@JvmName("fixExtendedSlots")
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun List<ExtendedSlot>.fix(): List<ExtendedSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slots as an extended slots.
 */
@JvmName("fixExtendedSlots")
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun Iterable<ExtendedSlot>.fix(): Iterable<ExtendedSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slot as an extended slot.
 */
inline fun Slot.fix(): ExtendedSlot {
    contract { returns() implies (this@fix is ExtendedSlot) }
    return this as ExtendedSlot
}

/**
 * Gets the normal slot as an extended slot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedSlot.fix(): ExtendedSlot = this

/**
 * Gets the normal slots as an extended slots.
 */
@JvmName("fixFilteringSlots")
inline fun List<FilteringSlot>.fix(): List<ExtendedFilteringSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slot as an extended slot.
 */
inline fun FilteringSlot.fix(): ExtendedFilteringSlot {
    contract { returns() implies (this@fix is ExtendedFilteringSlot) }
    return this as ExtendedFilteringSlot
}

/**
 * Gets the normal slot as an extended slot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedFilteringSlot.fix(): ExtendedFilteringSlot = this

/**
 * Gets the normal slots as an extended slots.
 */
@JvmName("fixInputSlots")
inline fun List<InputSlot>.fix(): List<ExtendedInputSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slot as an extended slot.
 */
inline fun InputSlot.fix(): ExtendedInputSlot {
    contract { returns() implies (this@fix is ExtendedInputSlot) }
    return this as ExtendedInputSlot
}

/**
 * Gets the normal slot as an extended slot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedInputSlot.fix(): ExtendedInputSlot = this

/**
 * Gets the normal slots as an extended slots.
 */
@JvmName("fixOutputSlots")
inline fun List<OutputSlot>.fix(): List<ExtendedOutputSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slot as an extended slot.
 */
inline fun OutputSlot.fix(): ExtendedOutputSlot {
    contract { returns() implies (this@fix is ExtendedOutputSlot) }
    return this as ExtendedOutputSlot
}

/**
 * Gets the normal slot as an extended slot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedOutputSlot.fix(): ExtendedOutputSlot = this

/**
 * Gets the normal slots as an extended slots.
 */
@JvmName("fixEquipmentSlots")
inline fun List<EquipmentSlot>.fix(): List<ExtendedEquipmentSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slot as an extended slot.
 */
inline fun EquipmentSlot.fix(): ExtendedEquipmentSlot {
    contract { returns() implies (this@fix is ExtendedEquipmentSlot) }
    return this as ExtendedEquipmentSlot
}

/**
 * Gets the normal slot as an extended slot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedEquipmentSlot.fix(): ExtendedEquipmentSlot = this

/**
 * Gets the normal slots as an extended slots.
 */
@JvmName("fixSidedSlots")
inline fun List<SidedSlot>.fix(): List<ExtendedSidedSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slot as an extended slot.
 */
inline fun SidedSlot.fix(): ExtendedSidedSlot {
    contract { returns() implies (this@fix is ExtendedSidedSlot) }
    return this as ExtendedSidedSlot
}

/**
 * Gets the normal slot as an extended slot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedSidedSlot.fix(): ExtendedSidedSlot = this

/**
 * Gets the normal slots as an extended slots.
 */
@JvmName("fixFuelSlots")
inline fun List<FuelSlot>.fix(): List<ExtendedFuelSlot> =
        this.uncheckedCast()

/**
 * Gets the normal slot as an extended slot.
 */
inline fun FuelSlot.fix(): ExtendedFuelSlot {
    contract { returns() implies (this@fix is ExtendedSidedSlot) }
    return this as ExtendedFuelSlot
}

/**
 * Gets the normal slot as an extended slot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedFuelSlot.fix(): ExtendedFuelSlot = this
