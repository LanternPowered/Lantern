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

import org.lanternpowered.api.data.Keys
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
    operator fun invoke(equipmentType: EquipmentType): Boolean

    override fun andThen(itemPredicate: ItemPredicate): EquipmentItemPredicate {
        val thisPredicate = this
        return object : EquipmentItemPredicate {
            override fun invoke(stack: ItemStack)
                    = thisPredicate(stack) && itemPredicate(stack)

            override fun invoke(type: ItemType)
                    = thisPredicate(type) && itemPredicate(type)

            override fun invoke(stack: ItemStackSnapshot)
                    = thisPredicate(stack) && itemPredicate(stack)

            override fun invoke(equipmentType: EquipmentType)
                    = thisPredicate(equipmentType) &&
                        (itemPredicate !is EquipmentItemPredicate || itemPredicate(equipmentType))
        }
    }

    override fun invert(): EquipmentItemPredicate {
        val thisPredicate = this
        return object : EquipmentItemPredicate {
            override fun invoke(equipmentType: EquipmentType) = !thisPredicate(equipmentType)
            override fun invoke(stack: ItemStack) = !thisPredicate(stack)
            override fun invoke(type: ItemType) = !thisPredicate(type)
            override fun invoke(stack: ItemStackSnapshot) = !thisPredicate(stack)
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
                override fun invoke(equipmentType: EquipmentType)
                        = predicate(equipmentType)

                override fun invoke(stack: ItemStack)
                        = stack.get(Keys.EQUIPMENT_TYPE).map { invoke(it) }.orElse(false)

                override fun invoke(stack: ItemStackSnapshot)
                        = stack.get(Keys.EQUIPMENT_TYPE).map { invoke(it) }.orElse(false)

                override fun invoke(type: ItemType)
                        = type.get(Keys.EQUIPMENT_TYPE).map { invoke(it) }.orElse(false)
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
