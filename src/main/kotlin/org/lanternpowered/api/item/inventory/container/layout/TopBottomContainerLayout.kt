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
 * Represents the layout of a top bottom container. The top part is
 * the opened inventory, e.g. chest. The bottom part is usually the
 * main inventory (storage grid and hotbar) of the player that opened
 * the inventory. This applies to most of the vanilla containers.
 */
interface TopBottomContainerLayout<T : ContainerLayout> : RootContainerLayout {

    /**
     * Gets the top part (opened inventory, e.g. chest grid) of the
     * top bottom container layout.
     */
    val top: T

    /**
     * Gets the top part (opened inventory, e.g. chest grid) of the
     * top bottom container layout and applies the given function.
     */
    fun top(fn: T.() -> Unit): T =
            this.top.apply(fn)

    /**
     * Gets the bottom part (storage grid + hotbar) of the top bottom
     * container layout.
     */
    val bottom: GridContainerLayout

    /**
     * Gets the bottom part (storage grid + hotbar) of the top bottom
     * container layout and applies the given function.
     */
    fun bottom(fn: GridContainerLayout.() -> Unit): GridContainerLayout =
            this.bottom.apply(fn)
}
