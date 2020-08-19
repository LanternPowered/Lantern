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

package org.lanternpowered.api.item.inventory

import kotlin.contracts.contract

typealias Container = org.spongepowered.api.item.inventory.Container

/**
 * Gets the normal container as an extended container.
 */
inline fun Container.fix(): ExtendedContainer {
    contract { returns() implies (this@fix is ExtendedContainer) }
    return this as ExtendedContainer
}

/**
 * Gets the normal container as an extended container.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedContainer.fix(): ExtendedContainer = this

/**
 * An extended version of [Container].
 */
interface ExtendedContainer : Container, ExtendedInventory
