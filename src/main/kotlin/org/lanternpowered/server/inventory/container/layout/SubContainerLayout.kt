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

import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.emptyText
import org.lanternpowered.server.util.collections.SlicedArrayIterator

open class SubContainerLayout(
        val offset: Int,
        override val size: Int,
        val base: LanternContainerLayout
) : ContainerLayout {

    override val title: Text
        get() = emptyText()

    override fun title(title: Text) {}

    override fun get(index: Int): ContainerSlot {
        if (index >= this.size)
            throw IndexOutOfBoundsException("Cannot get index (index=$index) from " +
                    "this layout (size=${this.size}).")
        return this.base.slots[this.offset + index]
    }

    override fun range(offset: Int, size: Int): ContainerLayout {
        if (offset >= this.size || size + offset >= this.size)
            throw IndexOutOfBoundsException("Cannot get range (offset=$offset,size=$size) from " +
                    "this layout (size=${this.size}).")
        return SubContainerLayout(this.offset + offset, size, this.base)
    }

    override fun range(range: IntRange): ContainerLayout {
        if (range.first >= this.size || range.last >= this.size)
            throw IndexOutOfBoundsException("Cannot get range ($range) from this layout (size=$size).")
        return SubContainerLayout(this.offset + range.first, range.last - range.first + 1, this.base)
    }

    override fun iterator(): Iterator<ContainerSlot> =
            SlicedArrayIterator(this.base.slots, this.offset, this.size)
}
