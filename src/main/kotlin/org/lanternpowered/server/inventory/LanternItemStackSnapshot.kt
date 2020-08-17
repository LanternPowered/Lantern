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

import net.kyori.adventure.text.event.HoverEvent
import org.lanternpowered.api.item.inventory.ExtendedItemStack
import org.lanternpowered.api.item.inventory.ExtendedItemStackSnapshot
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.DataQueries
import org.lanternpowered.server.data.MutableBackedSerializableLocalImmutableDataHolder
import org.lanternpowered.server.data.value.ValueFactory
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import java.util.function.UnaryOperator

class LanternItemStackSnapshot internal constructor(itemStack: LanternItemStack) :
        MutableBackedSerializableLocalImmutableDataHolder<ItemStackSnapshot, LanternItemStack>(itemStack),
        ExtendedItemStackSnapshot, TextRepresentable {

    override fun getType(): ItemType = this.backingDataHolder.type
    override fun getQuantity(): Int = this.backingDataHolder.quantity
    override fun isEmpty(): Boolean = this.backingDataHolder.isEmpty
    override fun asComponent(): Text = this.backingDataHolder.asComponent()

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

    override fun isSimilarTo(other: ItemStackSnapshot): Boolean =
            this.isSimilarTo((other as LanternItemStackSnapshot).backingDataHolder)

    override fun isSimilarTo(other: ItemStack): Boolean =
            this.backingDataHolder.isSimilarTo(other)

    override fun isEqualTo(other: ItemStack): Boolean =
            this.isSimilarTo(other) && this.quantity == other.quantity

    override fun isEqualTo(other: ItemStackSnapshot): Boolean =
            this.isSimilarTo(other) && this.quantity == other.quantity

    override fun asStack(): ExtendedItemStack = this.backingDataHolder

    override fun asHoverEvent(op: UnaryOperator<HoverEvent.ShowItem>): HoverEvent<HoverEvent.ShowItem> {
        TODO("Not yet implemented")
    }

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
