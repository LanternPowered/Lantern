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

import org.spongepowered.api.data.Keys
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.equipment.EquipmentGroup
import org.spongepowered.api.item.inventory.equipment.EquipmentType

interface EquipmentItemPredicate : ItemPredicate {

    /**
     * Tests whether the provided [EquipmentType] is valid.
     *
     * @param equipmentType The equipment type
     * @return Whether the equipment type is valid
     */
    fun test(equipmentType: EquipmentType): Boolean

    override fun andThen(itemPredicate: ItemPredicate): EquipmentItemPredicate {
        val thisPredicate = this
        return object : EquipmentItemPredicate {
            override fun test(stack: ItemStack)
                    = thisPredicate.test(stack) && itemPredicate.test(stack)

            override fun test(type: ItemType)
                    = thisPredicate.test(type) && itemPredicate.test(type)

            override fun test(stack: ItemStackSnapshot)
                    = thisPredicate.test(stack) && itemPredicate.test(stack)

            override fun test(equipmentType: EquipmentType)
                    = thisPredicate.test(equipmentType) &&
                        (itemPredicate !is EquipmentItemPredicate || itemPredicate.test(equipmentType))
        }
    }

    override fun invert(): EquipmentItemPredicate {
        val thisPredicate = this
        return object : EquipmentItemPredicate {
            override fun test(equipmentType: EquipmentType) = !thisPredicate.test(equipmentType)
            override fun test(stack: ItemStack) = !thisPredicate.test(stack)
            override fun test(type: ItemType) = !thisPredicate.test(type)
            override fun test(stack: ItemStackSnapshot) = !thisPredicate.test(stack)
        }
    }

    companion object {

        /**
         * Constructs a [ItemPredicate] for the provided
         * [EquipmentType] predicate.
         *
         * @param predicate The predicate
         * @return The equipment item filter
         */
        @JvmStatic
        fun of(predicate: (type: EquipmentType) -> Boolean): EquipmentItemPredicate {
            return object : EquipmentItemPredicate {
                override fun test(equipmentType: EquipmentType)
                        = predicate(equipmentType)

                override fun test(stack: ItemStack)
                        = stack.get(Keys.EQUIPMENT_TYPE).map { test(it) }.orElse(false)

                override fun test(stack: ItemStackSnapshot)
                        = stack.get(Keys.EQUIPMENT_TYPE).map { test(it) }.orElse(false)

                override fun test(type: ItemType)
                        = type.get(Keys.EQUIPMENT_TYPE).map { test(it) }.orElse(false)
            }
        }

        /**
         * Constructs a [ItemPredicate] for the given [EquipmentGroup].
         *
         * @param group The equipment group
         * @return The equipment item filter
         */
        @JvmStatic
        fun ofGroup(group: EquipmentGroup): EquipmentItemPredicate =
                this.of { type -> type.group == group }
    }
}
