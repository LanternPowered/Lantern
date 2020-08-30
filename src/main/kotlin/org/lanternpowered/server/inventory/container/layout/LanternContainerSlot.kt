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
package org.lanternpowered.server.inventory.container.layout

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.container.layout.ContainerButton
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.emptyItemStackSnapshot
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.stack.asStack
import org.lanternpowered.server.inventory.AbstractSlot

class LanternContainerSlot(
        val index: Int,
        private val layout: LanternContainerLayout
) : ContainerSlot {

    private var fillItem: ItemStackSnapshot = emptyItemStackSnapshot()
    private var _slot: AbstractSlot? = null
    private var _button: ContainerButton? = null

    val rawItem: ItemStack
        get() {
            val slot = this.slot
            if (slot != null)
                return slot.rawItem
            val button = this.button
            if (button != null)
                return button.icon.asStack()
            return this.fillItem.asStack()
        }

    val item: ItemStack
        get() {
            val slot = this.slot
            if (slot != null)
                return slot.peek()
            val button = this.button
            if (button != null)
                return button.icon.createStack()
            return this.fillItem.createStack()
        }

    val isBound: Boolean
        get() = this.slot != null || this.button != null

    override val slot: AbstractSlot?
        get() = this._slot

    override val button: ContainerButton?
        get() = this._button

    override fun fill(item: ItemStackSnapshot) {
        this.fillItem = item
        if (!this.isBound)
            this.queueSlotChange()
    }

    override fun button(): ContainerButton {
        TODO("Not yet implemented")
    }

    override fun button(fn: ContainerButton.() -> Unit): ContainerButton {
        TODO("Not yet implemented")
    }

    override fun slot(slot: Slot) {
        this.resetSlot()
        this._slot = slot as AbstractSlot
        this._button = null
        this.layout.addSlot(this, slot)
        this.queueSlotChange()
    }

    private fun resetSlot() {
        val slot = this.slot
        if (slot != null) {
            this.layout.removeSlot(this, slot)
            this._slot = null
        }
    }

    override fun reset() {
        if (!this.isBound)
            return
        this._button = null
        this.resetSlot()
        this.queueSlotChange()
    }

    private fun queueSlotChange() {
        this.layout.queueSilentSlotChangeSafely(this)
    }
}
