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
package org.lanternpowered.api.world.teleport

import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.world.Location
import org.spongepowered.api.entity.Entity
import java.util.Optional

typealias TeleportHelperFilter = org.spongepowered.api.world.teleport.TeleportHelperFilter

interface TeleportHelper : org.spongepowered.api.world.TeleportHelper {

    override fun getSafeLocation(location: Location, height: Int, width: Int, floorDistance: Int,
            filter: TeleportHelperFilter, vararg additionalFilters: TeleportHelperFilter): Optional<Location> {
        return getSafeLocation(location, height, width, floorDistance, listOf(filter) + additionalFilters.asList()).optional()
    }

    /**
     * Gets the next safe [Location] around the given location with a
     * given tolerance and search radius.
     *
     * Safe entails that the returned location will not be somewhere that
     * would harm an [Entity].
     *
     * It's possible the same location will be returned that was passed in.
     * This means it was safe.
     *
     * @param location The location to search nearby.
     * @param height The radius of blocks on the y-axis to search.
     * @param width The radius of blocks on the x and z-axis to search.
     * @param floorDistance The number of blocks below a selected block to
     *  search for a suitable floor, that is, the
     *  maximum distance to a floor that the selected
     *  point can be. If this is zero or negative, a floor
     *  check will not be performed.
     * @param filters The [TeleportHelperFilter]s to use to determine if a
     *  location is safe.
     * @return A safe location near the original location or the original
     *   location if it is deemed safe. If no safe location can be found,
     *  [Optional.empty] will be returned
     */
     fun getSafeLocation(location: Location, height: Int, width: Int, floorDistance: Int, filters: Iterable<TeleportHelperFilter>): Location?

}
