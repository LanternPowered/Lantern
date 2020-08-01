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

import org.lanternpowered.api.entity.spawn.EntitySpawner
import org.lanternpowered.api.world.weather.WeatherUniverse
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.util.AABB
import org.spongepowered.api.world.server.ServerWorld
import kotlin.contracts.contract
import org.spongepowered.api.world.World as SpongeWorld

typealias World = ServerWorld
typealias WorldProperties = org.spongepowered.api.world.storage.WorldProperties
typealias BlockChangeFlag = org.spongepowered.api.world.BlockChangeFlag
typealias BlockChangeFlags = org.spongepowered.api.world.BlockChangeFlags
typealias Locatable = org.spongepowered.api.world.Locatable
typealias LocatableBlock = org.spongepowered.api.world.LocatableBlock
typealias Location = org.spongepowered.api.world.ServerLocation
typealias WorldBorder = org.spongepowered.api.world.WorldBorder

/**
 * Gets the sponge world as a lantern world.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun SpongeWorld<*>.fix(): ExtendedWorld {
    contract {
        returns() implies (this@fix is ExtendedWorld)
    }
    return this as ExtendedWorld
}

/**
 * Gets the sponge world as a lantern world.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
@Suppress("NOTHING_TO_INLINE")
inline fun ExtendedWorld.fix(): ExtendedWorld = this

/**
 * The weather universe of the world, if it exists.
 */
val World.weatherUniverse: WeatherUniverse?
    get() = (this as ExtendedWorld).weatherUniverse

/**
 * The entity spawner of the world.
 */
inline val World.entitySpawner: EntitySpawner
    get() = (this as ExtendedWorld).entitySpawner

inline fun World.getIntersectingBlockCollisionBoxes(box: AABB): Set<AABB> {
    contract {
        returns() implies (this@getIntersectingBlockCollisionBoxes is ExtendedWorld)
    }
    return (this as ExtendedWorld).getIntersectingBlockCollisionBoxes(box)
}

inline fun World.getIntersectingEntities(box: AABB, noinline filter: (Entity) -> Boolean): Set<Entity> {
    contract {
        returns() implies (this@getIntersectingEntities is ExtendedWorld)
    }
    return (this as ExtendedWorld).getIntersectingEntities(box, filter)
}

/**
 * World extensions.
 */
interface ExtendedWorld : World {

    /**
     * The weather universe of this world, if it exists.
     */
    val weatherUniverse: WeatherUniverse?

    /**
     * The entity spawner of this world, if it exists.
     */
    val entitySpawner: EntitySpawner

    fun getIntersectingBlockCollisionBoxes(box: AABB): Set<AABB>

    fun getIntersectingEntities(box: AABB, filter: (Entity) -> Boolean): Set<Entity>
}
