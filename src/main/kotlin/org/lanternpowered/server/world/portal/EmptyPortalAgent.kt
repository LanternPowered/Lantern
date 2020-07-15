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

import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.world.Location
import org.lanternpowered.api.world.teleport.PortalAgentType

import java.util.Optional

class EmptyPortalAgent(portalAgentType: PortalAgentType) : LanternPortalAgent(portalAgentType) {

    override fun findPortal(targetLocation: Location): Optional<Location> = emptyOptional()
    override fun createPortal(targetLocation: Location): Optional<Location> = emptyOptional()
}
