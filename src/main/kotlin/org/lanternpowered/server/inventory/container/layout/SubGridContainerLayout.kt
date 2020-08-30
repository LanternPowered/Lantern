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

import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.container.layout.GridContainerLayout

class SubGridContainerLayout(
        offset: Int,
        override val width: Int,
        override val height: Int,
        base: LanternContainerLayout
) : SubContainerLayout(offset, width * height, base), GridContainerLayout {

    override fun get(x: Int, y: Int): ContainerSlot {
        if (x !in 0..this.width || y !in 0..this.height)
            throw IndexOutOfBoundsException("The position ($x,$y) is outside the layout grid (width=$width,height=$height).")
        return this[x + y * this.width]
    }

    override fun grid(x: Int, y: Int, width: Int, height: Int): GridContainerLayout {
        if (x !in 0..this.width || y !in 0..this.height ||
                (x + width) !in 0..this.width || (y + height) !in 0..this.height)
            throw IndexOutOfBoundsException("The grid (x=$x,y=$y,width=$width,height=$height) is outside the layout " +
                    "grid (width=${this.width},height=${this.height}).")
        return GridSubGridContainerLayout(x, y, width, height, this)
    }

    override fun column(x: Int): GridContainerLayout {
        if (x !in 0..this.width)
            throw IndexOutOfBoundsException("The column ($x) is outside the layout grid (width=$width,height=$height).")
        return GridSubGridContainerLayout(x, 0, 1, this.height, this)
    }

    override fun row(y: Int): GridContainerLayout {
        if (y !in 0..this.height)
            throw IndexOutOfBoundsException("The row ($y) is outside the layout grid (width=$width,height=$height).")
        return GridSubGridContainerLayout(0, y, this.width,1, this)
    }
}
