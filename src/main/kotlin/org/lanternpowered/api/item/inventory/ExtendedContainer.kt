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

import org.lanternpowered.api.item.inventory.container.Container
import org.lanternpowered.api.item.inventory.container.ExtendedContainer
import kotlin.contracts.contract

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
