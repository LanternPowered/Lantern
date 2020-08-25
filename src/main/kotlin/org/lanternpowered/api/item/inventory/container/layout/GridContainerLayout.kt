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
package org.lanternpowered.api.item.inventory.container.layout

/**
 * A container layout that is a grid, this includes
 * rows and columns.
 */
interface GridContainerLayout : ContainerLayout {

    /**
     * The width (number of columns) of the grid.
     */
    val width: Int

    /**
     * The height (number of rows) of the grid.
     */
    val height: Int

    /**
     * Gets the [ContainerSlot] at the given x and y position.
     */
    operator fun get(x: Int, y: Int): ContainerSlot

    /**
     * Gets a sub layout for the given y (row) index.
     */
    fun row(y: Int): GridContainerLayout

    /**
     * Gets a sub layout for the given x (column) index.
     */
    fun column(x: Int): GridContainerLayout

    /**
     * Gets a sub grid layout for the given start coordinates and bounds.
     *
     * @param x The start x coordinate
     * @param y The start y coordinate
     * @param width The width of the grid
     * @param height The height of the grid
     * @return The sub grid layout
     */
    fun grid(x: Int, y: Int, width: Int, height: Int): GridContainerLayout
}
