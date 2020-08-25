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
@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package org.lanternpowered.api.item.inventory.container

import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import kotlin.contracts.contract

typealias ContainerType = org.spongepowered.api.item.inventory.ContainerType

/**
 * Gets the normal container as an extended container.
 */
inline fun ContainerType.fix(): ExtendedContainerType<ContainerLayout> {
    contract { returns() implies (this@fix is ExtendedContainer) }
    return this as ExtendedContainerType<ContainerLayout>
}

/**
 * Gets the normal container as an extended container.
 */
inline fun <L : ContainerLayout> ContainerType.fixWithLayout(): ExtendedContainerType<L> {
    contract { returns() implies (this@fixWithLayout is ExtendedContainer) }
    return this as ExtendedContainerType<L>
}

/**
 * Gets the normal container as an extended container.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun <L : ContainerLayout> ExtendedContainerType<L>.fix(): ExtendedContainerType<L> = this

/**
 * An extended version of [ContainerType].
 */
interface ExtendedContainerType<L : ContainerLayout> : ContainerType {

}
