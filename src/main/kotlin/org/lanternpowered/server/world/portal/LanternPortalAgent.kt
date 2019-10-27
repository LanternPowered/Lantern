/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.world.portal

import org.spongepowered.api.world.Location
import org.spongepowered.api.world.teleport.PortalAgent
import org.spongepowered.api.world.teleport.PortalAgentType
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
        val optLoc = this.findPortal(targetLocation)
        return if (optLoc.isPresent) optLoc else this.createPortal(targetLocation)
    }

    override fun getType(): PortalAgentType = this.portalAgentType
}