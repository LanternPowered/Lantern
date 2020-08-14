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
typealias DataViewSafetyMode = org.spongepowered.api.data.persistence.DataView.SafetyMode
typealias DataContainer = org.spongepowered.api.data.persistence.DataContainer

/**
 * Gets or creates a [DataView] at the given path.
 */
fun DataView.getOrCreateView(path: DataQuery): DataView =
        getView(path).orElseGet { createView(path) }

/**
 * Moves data in the view from the source to the destination path.
 *
 * @param source The source path
 * @param destination The destination path
 * @return Whether the data was present at the source path
 */
fun DataView.move(source: DataQuery, destination: DataQuery): Boolean {
    val originalValue = get(source).orNull() ?: return false
    set(destination, originalValue)
    return true
}
