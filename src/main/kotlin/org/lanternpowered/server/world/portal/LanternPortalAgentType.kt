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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.world.World
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.world.LanternWorld
import org.spongepowered.api.world.teleport.PortalAgent
import org.spongepowered.api.world.teleport.PortalAgentType

class LanternPortalAgentType<T : PortalAgent>(
        key: CatalogKey,
        private val portalAgentClass: Class<T>,
        private val supplier: (World<*>, LanternPortalAgentType<T>) -> T
) : DefaultCatalogType(key), PortalAgentType {

    /**
     * Creates a [T] for the specified [LanternWorld].
     *
     * @param world The target world
     * @return The portal agent instance
     */
    fun newPortalAgent(world: LanternWorld): T {
        return this.supplier(world, this)
    }

    override fun getPortalAgentClass() = this.portalAgentClass
    override fun toStringHelper() = super.toStringHelper()
            .add("portalAgentClass", this.portalAgentClass)
}
