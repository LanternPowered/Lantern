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
package org.lanternpowered.api.world

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.registry.builderOf

typealias WorldArchetype = org.spongepowered.api.world.WorldArchetype
typealias WorldArchetypeBuilder = org.spongepowered.api.world.WorldArchetype.Builder
typealias WorldArchetypes = org.spongepowered.api.world.WorldArchetypes

/**
 * Constructs a new [WorldArchetype] with the given [key] and applied settings.
 */
fun worldArchetypeOf(key: ResourceKey, fn: WorldArchetypeBuilder.() -> Unit): WorldArchetype =
        builderOf<WorldArchetypeBuilder>().key(key).apply(fn).build()
