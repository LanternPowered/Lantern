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
package org.lanternpowered.api.data.persistence

import org.lanternpowered.api.util.optional.orNull

typealias DataQuery = org.spongepowered.api.data.persistence.DataQuery
typealias DataView = org.spongepowered.api.data.persistence.DataView
typealias DataContainer = org.spongepowered.api.data.persistence.DataContainer

fun DataView.getOrCreateView(path: DataQuery): DataView =
        getView(path).orElseGet { createView(path) }

fun DataView.move(origin: DataQuery, destination: DataQuery): Boolean {
    val originalValue = get(origin).orNull() ?: return false
    set(destination, originalValue)
    return true
}
