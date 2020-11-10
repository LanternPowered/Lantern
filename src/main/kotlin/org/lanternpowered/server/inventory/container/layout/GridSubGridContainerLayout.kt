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
import org.lanternpowered.api.item.inventory.container.layout.GridContainerLayout
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.emptyText

class GridSubGridContainerLayout(
        private val offsetX: Int,
        private val offsetY: Int,
        override val width: Int,
        override val height: Int,
        private val base: GridContainerLayout
) : GridContainerLayout {

    override var title: Text
        get() = emptyText()
        set(_) {}

    override val size: Int = this.width * this.height

    override fun get(x: Int, y: Int): ContainerSlot {
        if (x !in 0..this.width || y !in 0..this.height)
            throw IndexOutOfBoundsException("The position ($x,$y) is outside the layout grid (width=$width,height=$height).")
        return this.getUnsafe(x, y)
    }

    fun getUnsafe(x: Int, y: Int): ContainerSlot = this.base[this.offsetX + x, this.offsetY + y]

    override fun get(index: Int): ContainerSlot {
        if (index !in 0 until this.size)
            throw IndexOutOfBoundsException("The index ($index) is outside the grid (width=$width,height=$height,size=$size).")
        val x = index % this.width
        val y = index / this.width
        return this.base[this.offsetX + x, this.offsetY + y]
    }

    override fun row(y: Int): GridContainerLayout {
        if (y !in 0..this.height)
            throw IndexOutOfBoundsException("The row ($y) is outside the grid (width=$width,height=$height).")
        return GridSubGridContainerLayout(this.offsetX, this.offsetY + y, this.width, 1, this.base)
    }

    override fun column(x: Int): GridContainerLayout {
        if (x !in 0..this.width)
            throw IndexOutOfBoundsException("The column ($x) is outside the grid (width=$width,height=$height).")
        return GridSubGridContainerLayout(this.offsetX + x, this.offsetY, 1, this.height, this.base)
    }

    override fun grid(x: Int, y: Int, width: Int, height: Int): GridContainerLayout {
        if (x !in 0..this.width || y !in 0..this.height ||
                (x + width) !in 0..this.width || (y + height) !in 0..this.height)
            throw IndexOutOfBoundsException("The grid (x=$x,y=$y,width=$width,height=$height) is outside the layout " +
                    "grid (width=${this.width},height=${this.height}).")
        return GridSubGridContainerLayout(this.offsetX + x, this.offsetY + y, this.width, this.height, this.base)
    }

    override fun range(offset: Int, size: Int): ContainerLayout {
        if (offset >= this.size || size + offset >= this.size)
            throw IndexOutOfBoundsException("Cannot get range (offset=$offset,size=$size) from " +
                    "this layout (size=${this.size}).")
        return GridSubGridContainerLayout(offset, 0, size, 1, this)
    }

    override fun range(range: IntRange): ContainerLayout {
        if (range.first >= this.size || range.last >= this.size)
            throw IndexOutOfBoundsException("Cannot get range ($range) from this layout (size=$size).")
        return GridSubGridContainerLayout(range.first, 0, range.last - range.first + 1, 1, this)
    }

    override fun iterator(): Iterator<ContainerSlot> {
        return sequence {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    yield(getUnsafe(x, y))
                }
            }
        }.iterator()
    }
}
