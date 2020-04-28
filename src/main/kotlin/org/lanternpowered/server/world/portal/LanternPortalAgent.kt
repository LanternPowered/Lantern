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

import org.lanternpowered.api.world.teleport.PortalAgent
import org.lanternpowered.api.world.teleport.PortalAgentType
import org.spongepowered.api.world.Location
import java.util.Optional

abstract class LanternPortalAgent(private val portalAgentType: PortalAgentType) : PortalAgent {

    private var searchRadius: Int = 0
    private var creationRadius: Int = 0

    override fun getSearchRadius() = this.searchRadius

    override fun setSearchRadius(radius: Int) = apply {
        this.searchRadius = radius
    }

    override fun getCreationRadius() = this.creationRadius

    override fun setCreationRadius(radius: Int) = apply {
        this.creationRadius = radius
    }

    override fun findOrCreatePortal(targetLocation: Location): Optional<Location> {
        val optLoc = findPortal(targetLocation)
        return if (optLoc.isPresent) optLoc else createPortal(targetLocation)
    }

    override fun getType(): PortalAgentType = this.portalAgentType
}
