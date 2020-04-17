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

import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.data.DataManipulator
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import java.util.Optional

class LanternItemStackBuilder : AbstractDataBuilder<ItemStack>(ItemStack::class.java, 1), ItemStack.Builder {

    private var itemStack: LanternItemStack? = null
    private var itemTypeSet: Boolean = false

    private fun itemStack(itemType: ItemType?): LanternItemStack {
        var itemStack = this.itemStack
        if (itemType != null) {
            if (itemStack == null) {
                itemStack = LanternItemStack(itemType)
            } else if (itemStack.type !== itemType) {
                val oldItemStack = itemStack
                itemStack = LanternItemStack(itemType)
                itemStack.quantity = oldItemStack.quantity
                itemStack.copyFromNoEvents(oldItemStack, MergeFunction.REPLACEMENT_PREFERRED)
            }
            this.itemTypeSet = true
        } else if (itemStack == null) {
            itemStack = LanternItemStack(ItemTypes.APPLE.get())
        }
        this.itemStack = itemStack
        return itemStack
    }

    override fun itemType(itemType: ItemType) = apply {
        itemStack(itemType)
    }

    override fun getCurrentItem(): ItemType = if (this.itemTypeSet) itemStack(null).type else ItemTypes.AIR

    override fun quantity(quantity: Int) = apply {
        itemStack(null).quantity = quantity
    }

    override fun add(itemData: DataManipulator) = apply {
        val itemStack = itemStack(null)
        itemData.values.forEach { itemStack.offerFastNoEvents(it) }
    }

    override fun add(value: Value<*>) = apply {
        itemStack(null).offerFastNoEvents(value)
    }

    override fun <V : Any> add(key: Key<out Value<V>>, value: V) = apply {
        itemStack(null).offerFastNoEvents(key, value)
    }

    override fun from(value: ItemStack) = apply {
        this.itemStack = value.copy() as LanternItemStack
    }

    override fun fromSnapshot(snapshot: ItemStackSnapshot) = apply {
        from((snapshot as LanternItemStackSnapshot).unwrap())
    }

    override fun fromItemStack(itemStack: ItemStack) = apply {
        from(itemStack)
    }

    override fun fromContainer(container: DataView): ItemStack.Builder {
        TODO()
    }

    override fun fromBlockSnapshot(blockSnapshot: BlockSnapshot): ItemStack.Builder {
        TODO()
    }

    override fun reset() = apply {
        this.itemTypeSet = false
        this.itemStack = null
    }

    override fun build(): ItemStack {
        check(this.itemTypeSet) { "The item type must be set" }
        return itemStack(null).copy()
    }

    override fun buildContent(container: DataView): Optional<ItemStack> {
        TODO()
    }
}
