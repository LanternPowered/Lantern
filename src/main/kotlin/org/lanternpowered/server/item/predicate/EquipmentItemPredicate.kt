/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.item.predicate

import org.spongepowered.api.data.property.Properties
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
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
                        = stack.getProperty(Properties.EQUIPMENT_TYPE).map { test(it) }.orElse(false)

                override fun test(stack: ItemStackSnapshot)
                        = stack.getProperty(Properties.EQUIPMENT_TYPE).map { test(it) }.orElse(false)

                override fun test(type: ItemType)
                        = type.getProperty(Properties.EQUIPMENT_TYPE).map { test(it) }.orElse(false)
            }
        }

        /**
         * Constructs a [ItemPredicate] for the provided
         * [EquipmentType] predicate.
         *
         * @param equipmentType The equipment type
         * @return The equipment item filter
         */
        @JvmStatic
        fun of(equipmentType: EquipmentType): EquipmentItemPredicate {
            return of(equipmentType::includes)
        }
    }
}
