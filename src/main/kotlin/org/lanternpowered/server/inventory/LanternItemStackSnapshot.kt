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
package org.lanternpowered.server.inventory

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.DataQueries
import org.lanternpowered.server.data.LocalDataHolderHelper
import org.lanternpowered.server.data.MutableBackedSerializableLocalImmutableDataHolder
import org.lanternpowered.server.data.value.ValueFactory
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot

class LanternItemStackSnapshot internal constructor(itemStack: LanternItemStack) :
        MutableBackedSerializableLocalImmutableDataHolder<ItemStackSnapshot, LanternItemStack>(itemStack), ItemStackSnapshot {

    override fun getType(): ItemType = this.backingDataHolder.type
    override fun getQuantity(): Int = this.backingDataHolder.quantity
    override fun isEmpty() = this.backingDataHolder.isEmpty
    override fun getTranslation() = this.backingDataHolder.translation

    override fun createStack() = this.backingDataHolder.copy()

    override fun toContainer(): DataContainer {
        return super.toContainer()
                .set(DataQueries.ITEM_TYPE, type)
                .set(DataQueries.QUANTITY, quantity)
    }

    override fun withBacking(backingDataHolder: LanternItemStack) = LanternItemStackSnapshot(backingDataHolder)

    override fun toString() = ToStringHelper(this)
            .add("type", this.type.key)
            .add("quantity", this.quantity)
            .add("data", ValueFactory.toString(this.backingDataHolder))
            .toString()

    /**
     * Gets whether the specified [ItemStackSnapshot] is similar
     * to this [ItemStackSnapshot]. The [ItemType] and all
     * the applied data must match.
     *
     * @param that The other snapshot
     * @return Is similar
     */
    fun similarTo(that: ItemStackSnapshot): Boolean {
        return similarTo((that as LanternItemStackSnapshot).backingDataHolder)
    }

    /**
     *
     * Gets whether the specified [ItemStack] is similar
     * to this [ItemStackSnapshot]. The [ItemType] and all
     * the applied data must match.
     *
     * @param that The other snapshot
     * @return Is similar
     */
    fun similarTo(that: ItemStack): Boolean {
        return type === that.type && LocalDataHolderHelper.matchContents(this.backingDataHolder, that as LanternItemStack)
    }

    /**
     * Gets the internal [LanternItemStack],
     * internal use only to avoid copying. The returned
     * stack may never be modified.
     *
     * @return The internal stack
     */
    fun unwrap() = this.backingDataHolder

    companion object {

        /**
         * Gets the [] as a [LanternItemStackSnapshot].
         *
         * @return The none item stack snapshot
         */
        @JvmStatic
        fun none(): LanternItemStackSnapshot {
            return ItemStackSnapshot.empty() as LanternItemStackSnapshot
        }

        /**
         * Creates [LanternItemStackSnapshot] by wrapping the [ItemStack],
         * this DOES NOT COPY the [ItemStack]. Use [ItemStack.createSnapshot]
         * in that case. This method may only be used with extra care, only when the
         * [ItemStack] you are working with won't change anymore, is "final".
         *
         * @param itemStack The item stack
         * @return The item stack snapshot
         */
        @JvmStatic
        fun wrap(itemStack: ItemStack): LanternItemStackSnapshot {
            // Reuse the none item stack snapshot if possible
            return if (itemStack.isEmpty) {
                ItemStackSnapshot.empty() as LanternItemStackSnapshot
            } else LanternItemStackSnapshot(itemStack as LanternItemStack)
        }
    }
}
