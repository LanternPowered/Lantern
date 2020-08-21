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

package org.lanternpowered.api.item.inventory.entity

import org.lanternpowered.api.item.inventory.ExtendedGridInventory
import org.lanternpowered.api.item.inventory.ExtendedInventory
import org.lanternpowered.api.item.inventory.hotbar.ExtendedHotbar

typealias PrimaryPlayerInventory = org.spongepowered.api.item.inventory.entity.PrimaryPlayerInventory

/**
 * An extended version of [PrimaryPlayerInventory].
 */
interface ExtendedPrimaryPlayerInventory : ExtendedInventory, PrimaryPlayerInventory {

    override fun getHotbar(): ExtendedHotbar

    override fun getStorage(): ExtendedGridInventory

    override fun asGrid(): ExtendedGridInventory
}
