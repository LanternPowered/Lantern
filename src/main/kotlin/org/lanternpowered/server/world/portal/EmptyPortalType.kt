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
package org.lanternpowered.server.world.portal

import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.key.lanternKey
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.portal.Portal
import java.util.Optional

object EmptyPortalType : LanternPortalType(lanternKey("empty")) {
    override fun teleport(entity: Entity, destination: Location, generateDestinationPortal: Boolean): Boolean = false
    override fun generatePortal(location: Location) {}
    override fun findPortal(location: Location): Optional<Portal> = emptyOptional()
}
