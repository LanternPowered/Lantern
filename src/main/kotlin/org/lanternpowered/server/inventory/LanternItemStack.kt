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
import org.lanternpowered.server.data.LocalKeyRegistry
import org.lanternpowered.server.data.LocalMutableDataHolder
import org.lanternpowered.server.data.property.PropertyHolderBase
import org.lanternpowered.server.data.value.ValueFactory
import org.lanternpowered.server.item.LanternItemType
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.text.translation.Translation
import java.util.function.Consumer

class LanternItemStack private constructor(
        private val itemType: ItemType,
        private var quantity: Int,
        override val keyRegistry: LocalKeyRegistry<LanternItemStack>
) : ItemStack, PropertyHolderBase, LocalMutableDataHolder {

    /**
     * Gets whether this item stack is filled. (non empty)
     *
     * @return Is not empty
     */
    val isNotEmpty: Boolean
        get() = !isEmpty

    /**
     * Constructs a new [LanternItemStack] for the specified [ItemType].
     *
     * @param itemType The item type
     * @param quantity The quantity
     */
    @JvmOverloads
    constructor(itemType: ItemType, quantity: Int = 1) : this(itemType, quantity, LocalKeyRegistry.of()) {
        check(quantity >= 0) { "quantity may not be negative" }
        registerKeys()
    }

    private fun registerKeys() {
        keyRegistry {
            register(Keys.DISPLAY_NAME)
            register(Keys.ITEM_LORE, mutableListOf())
            register(Keys.BREAKABLE_BLOCK_TYPES, mutableSetOf())
            register(Keys.ITEM_ENCHANTMENTS, mutableListOf())
        }
    }

    override fun validateRawData(dataView: DataView): Boolean {
        return dataView.contains(DataQueries.ITEM_TYPE)
    }

    override fun setRawData(dataView: DataView) {
        dataView.remove(DataQueries.ITEM_TYPE)
        this.quantity = dataView.getInt(DataQueries.QUANTITY).orElse(1)
        super.setRawData(dataView)
    }

    override fun toContainer(): DataContainer = super.toContainer()
            .set(DataQueries.ITEM_TYPE, this.type)
            .set(DataQueries.QUANTITY, this.quantity)

    override fun getTranslation(): Translation {
        return (type as LanternItemType).translationProvider.get(this.itemType, this)
    }

    override fun getType(): ItemType = if (this.quantity == 0) ItemTypes.AIR else this.itemType

    override fun getMaxStackQuantity(): Int = this.type.maxStackQuantity
    override fun getQuantity(): Int = if (this.itemType === ItemTypes.AIR) 0 else this.quantity

    override fun setQuantity(quantity: Int) {
        check(quantity >= 0) { "quantity may not be negative" }
        this.quantity = quantity
    }

    override fun createSnapshot(): ItemStackSnapshot {
        return if (isEmpty) {
            ItemStackSnapshot.empty()
        } else LanternItemStackSnapshot(copy())
    }

    fun toSnapshot(): ItemStackSnapshot = createSnapshot()

    fun toWrappedSnapshot(): ItemStackSnapshot {
        return if (isEmpty && empty != null) {
            ItemStackSnapshot.empty()
        } else LanternItemStackSnapshot(this)
    }

    override fun equalTo(that: ItemStack): Boolean {
        return similarTo(that) && getQuantity() == that.quantity
    }

    /**
     * Similar to [.equalTo], but matches this
     * [ItemStack] with a [ItemStackSnapshot].
     *
     * @param that The other snapshot
     * @return Is equal
     */
    fun equalTo(that: ItemStackSnapshot): Boolean {
        return similarTo(that) && getQuantity() == that.quantity
    }

    override fun isEmpty(): Boolean {
        return this.itemType === ItemTypes.AIR || this.quantity <= 0
    }

    override fun copy(): LanternItemStack {
        // Just return the empty instance
        return if (isEmpty) {
            empty
        } else LanternItemStack(this.itemType, this.quantity, keyRegistry.copy())
    }

    /**
     * Applies the [Consumer] when this item stack isn't empty.
     *
     * @param consumer The consumer to accept
     * @see .isEmpty
     */
    fun ifNotEmpty(consumer: Consumer<LanternItemStack>) {
        if (!isEmpty) {
            consumer.accept(this)
        }
    }

    /**
     * Gets whether the specified [ItemStackSnapshot] is similar
     * to this [ItemStack]. The [ItemType] and all
     * the applied data must match.
     *
     * @param that The other snapshot
     * @return Is similar
     */
    fun similarTo(that: ItemStackSnapshot): Boolean {
        return similarTo((that as LanternItemStackSnapshot).unwrap())
    }

    /**
     *
     * Gets whether the specified [ItemStack] is similar
     * to this [ItemStack]. The [ItemType] and all
     * the applied data must match.
     *
     * @param that The other snapshot
     * @return Is similar
     */
    fun similarTo(that: ItemStack): Boolean {
        val emptyA = isEmpty
        val emptyB = that.isEmpty
        return if (emptyA != emptyB) {
            emptyA && emptyB
        } else type === that.type && LocalDataHolderHelper.matchContents(this, that as LanternItemStack)
    }

    override fun toString() = ToStringHelper(this)
            .add("type", this.type.key)
            .add("quantity", this.quantity)
            .add("data", ValueFactory.toString(this))
            .toString()

    companion object {

        private val empty by lazy { LanternItemStack(ItemTypes.AIR) }

        /**
         * Gets a empty [ItemStack] if the specified [ItemStack]
         * is `null`. Otherwise returns the item stack itself.
         *
         * @param itemStack The item stack
         * @return A empty or the provided item stack
         */
        @JvmStatic
        fun orEmpty(itemStack: ItemStack?): LanternItemStack
            = if (itemStack == null) this.empty else itemStack as LanternItemStack

        /**
         * Gets a empty [ItemStack].
         *
         * A empty item stack will always have the item type
         * [ItemTypes.AIR] and a quantity of `0`.
         * And any data offered to it will be rejected.
         *
         * @return The empty item stack
         */
        @JvmStatic
        fun empty(): LanternItemStack
            = this.empty

        @JvmStatic
        fun isEmpty(itemStack: ItemStack?): Boolean {
            return itemStack == null || itemStack.isEmpty
        }

        @JvmStatic
        fun areSimilar(itemStackA: ItemStack?, itemStackB: ItemStack?): Boolean {
            return if (itemStackA === itemStackB)
                true
            else if (itemStackA == null || itemStackB == null)
                false
            else
                (itemStackA as LanternItemStack).similarTo(itemStackB)
        }

        @JvmStatic
        fun areSimilar(itemStackA: ItemStack?, itemStackB: ItemStackSnapshot?): Boolean {
            return if (itemStackA === (itemStackB as LanternItemStackSnapshot).unwrap())
                true
            else if (itemStackA == null || itemStackB == null)
                false
            else
                (itemStackA as LanternItemStack).similarTo(itemStackB)
        }

        @JvmStatic
        fun areSimilar(itemStackA: ItemStackSnapshot?, itemStackB: ItemStack?): Boolean {
            return if (itemStackB === (itemStackA as LanternItemStackSnapshot).unwrap())
                true
            else if (itemStackA == null || itemStackB == null)
                false
            else
                (itemStackB as LanternItemStack).similarTo(itemStackA)
        }

        @JvmStatic
        fun areSimilar(itemStackA: ItemStackSnapshot?, itemStackB: ItemStackSnapshot?): Boolean {
            return if ((itemStackA as LanternItemStackSnapshot).unwrap() === (itemStackA as LanternItemStackSnapshot).unwrap())
                true
            else if (itemStackA == null || itemStackB == null) false else (itemStackB as LanternItemStackSnapshot).unwrap().similarTo(itemStackA)
        }

        @JvmStatic
        fun toNullable(itemStackSnapshot: ItemStackSnapshot?): LanternItemStack? {
            return if (itemStackSnapshot == null || itemStackSnapshot.isEmpty) null else itemStackSnapshot.createStack() as LanternItemStack
        }

        @JvmStatic
        fun toNullable(itemStack: ItemStack?): LanternItemStack? {
            return if (itemStack == null || itemStack.isEmpty) null else itemStack as LanternItemStack?
        }

        @JvmStatic
        fun toSnapshot(itemStack: ItemStack?): ItemStackSnapshot {
            return if (itemStack == null || itemStack.isEmpty) ItemStackSnapshot.empty() else itemStack.createSnapshot()
        }
    }
}
