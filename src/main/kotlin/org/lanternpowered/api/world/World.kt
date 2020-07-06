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

import org.lanternpowered.api.world.weather.WeatherUniverse
import org.spongepowered.api.world.server.ServerWorld
import org.spongepowered.api.world.World as SpongeWorld
import kotlin.contracts.contract

typealias World = ServerWorld
typealias WorldProperties = org.spongepowered.api.world.storage.WorldProperties
typealias BlockChangeFlag = org.spongepowered.api.world.BlockChangeFlag
typealias BlockChangeFlags = org.spongepowered.api.world.BlockChangeFlags
typealias Locatable = org.spongepowered.api.world.Locatable
typealias LocatableBlock = org.spongepowered.api.world.LocatableBlock
typealias Location = org.spongepowered.api.world.Location
typealias WorldBorder = org.spongepowered.api.world.WorldBorder

/**
 * Gets the sponge world as a lantern world.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun SpongeWorld<*>.fix(): World {
    contract {
        returns() implies (this@fix is World)
    }
    return this as World
}

/**
 * Gets the sponge world as a lantern world.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
@Suppress("NOTHING_TO_INLINE")
inline fun World.fix(): World = this

/**
 * The weather universe of the world, if it exists.
 */
val World.weatherUniverse: WeatherUniverse?
    get() = (this as ExtendedWorld).weatherUniverse

/**
 * World extensions.
 */
interface ExtendedWorld : ServerWorld {

    /**
     * The weather universe of the world, if it exists.
     */
    val weatherUniverse: WeatherUniverse?
}
