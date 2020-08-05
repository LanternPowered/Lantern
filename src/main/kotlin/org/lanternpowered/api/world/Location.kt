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

package org.lanternpowered.api.world

import org.lanternpowered.api.key.NamespacedKey
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i

typealias Location = org.spongepowered.api.world.ServerLocation

inline fun locationOf(world: World, position: Vector3d): Location =
        Location.of(world, position)

inline fun locationOf(world: World, position: Vector3i): Location =
        Location.of(world, position)

inline fun locationOf(worldKey: NamespacedKey, position: Vector3d): Location =
        Location.of(worldKey, position)

inline fun locationOf(worldKey: NamespacedKey, position: Vector3i): Location =
        Location.of(worldKey, position)
