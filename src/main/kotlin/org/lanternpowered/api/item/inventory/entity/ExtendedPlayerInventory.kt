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

typealias PlayerInventory = org.spongepowered.api.item.inventory.entity.PlayerInventory

/**
 * An extended version of [PlayerInventory].
 */
interface ExtendedPlayerInventory : ExtendedStandardInventory, PlayerInventory
