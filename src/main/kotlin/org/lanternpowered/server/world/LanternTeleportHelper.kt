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
package org.lanternpowered.server.world

import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.teleport.TeleportHelper
import org.lanternpowered.api.world.teleport.TeleportHelperFilter

object LanternTeleportHelper : TeleportHelper {

    override fun getSafeLocation(
            location: Location, height: Int, width: Int, floorDistance: Int, filters: Iterable<TeleportHelperFilter>
    ): Location? {
        return null // TODO
    }
}
