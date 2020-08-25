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
 * Represents the top container layout of a loom.
 */
interface LoomContainerLayout : ContainerLayout {

    /**
     * The banner input slot.
     */
    fun banner(): ContainerSlot

    /**
     * The dye input slot.
     */
    fun dye(): ContainerSlot

    /**
     * The pattern input slot.
     */
    fun pattern(): ContainerSlot

    /**
     * The output slot.
     */
    fun output(): ContainerSlot
}
