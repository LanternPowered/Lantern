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
package org.lanternpowered.server.inventory

import org.spongepowered.api.block.BlockSnapshot
import org.spongepowered.api.data.DataManipulator
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.value.MergeFunction
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.entity.attribute.AttributeModifier
import org.spongepowered.api.entity.attribute.type.AttributeType
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.equipment.EquipmentType
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

    override fun getCurrentItem(): ItemType = if (this.itemTypeSet) itemStack(null).type else ItemTypes.AIR.get()

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

    override fun attributeModifier(attributeType: AttributeType, modifier: AttributeModifier, equipmentType: EquipmentType): ItemStack.Builder {
        TODO("Not yet implemented")
    }

    override fun from(value: ItemStack) = apply {
        this.itemStack = value.copy() as LanternItemStack
    }

    override fun fromSnapshot(snapshot: ItemStackSnapshot) = apply {
        from((snapshot as LanternItemStackSnapshot).asStack())
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
