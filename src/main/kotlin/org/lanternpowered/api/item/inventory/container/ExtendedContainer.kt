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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.item.inventory.container

import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout

typealias Container = org.spongepowered.api.item.inventory.Container

/**
 * An extended version of [Container].
 */
interface ExtendedContainer : Container, ExtendedInventory {

    /**
     * The current layout of the container. The layout can be modified
     * anytime you want.
     */
    var layout: ContainerLayout
}
