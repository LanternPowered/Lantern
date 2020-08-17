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

import org.lanternpowered.api.item.inventory.ExtendedInventory2D
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.Slot
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.value.Value
import java.util.Optional

abstract class AbstractInventory2D : AbstractChildrenInventory(), ExtendedInventory2D {

    private var _width = 0
    private var _height = 0

    override val width: Int get() = this._width
    override val height: Int get() = this._height

    protected fun init(children: List<AbstractMutableInventory>, slots: List<AbstractSlot>, width: Int, height: Int) {
        this.initSize(slots.size, width, height)
        super.init(children, slots)
    }

    protected fun init(children: List<AbstractMutableInventory>, width: Int, height: Int) {
        this.init(children, children.asSequence().slots().toImmutableList(), width, height)
    }

    private fun initSize(slots: Int, width: Int, height: Int) {
        check(width >= 1) { "Width must be at least 1, and not $width" }
        check(height >= 1) { "Height must be at least 1, and not $height" }

        val expectedSize = width * height
        if (expectedSize != slots)
            error("Slots mismatch, $width (width) * $height (height) = $expectedSize (slots) and not $slots (slots)")

        this._width = width
        this._height = height
    }

    override fun init(children: List<AbstractMutableInventory>, slots: List<AbstractSlot>): Unit =
            throw UnsupportedOperationException()

    override fun init(children: List<AbstractMutableInventory>): Unit =
            throw UnsupportedOperationException()

    override fun <V : Any> get(child: Inventory, key: Key<out Value<V>>): Optional<V> {
        if (key == Keys.SLOT_POSITION.get() && child is Slot)
            return this.slotPosition(child).asOptional().uncheckedCast()
        return super<AbstractChildrenInventory>.get(child, key)
    }
}
