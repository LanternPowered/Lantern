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
package org.lanternpowered.server.world.portal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.teleport.PortalAgent;
import org.spongepowered.api.world.teleport.PortalAgentType;

import java.util.Optional;

public abstract class LanternPortalAgent implements PortalAgent {

    private final PortalAgentType portalAgentType;

    protected int searchRadius;
    protected int creationRadius;

    public LanternPortalAgent(PortalAgentType portalAgentType) {
        this.portalAgentType = checkNotNull(portalAgentType, "portalAgentType");
    }

    @Override
    public int getSearchRadius() {
        return this.searchRadius;
    }

    @Override
    public LanternPortalAgent setSearchRadius(int radius) {
        this.searchRadius = radius;
        return this;
    }

    @Override
    public int getCreationRadius() {
        return this.creationRadius;
    }

    @Override
    public LanternPortalAgent setCreationRadius(int radius) {
        this.creationRadius = radius;
        return this;
    }

    @Override
    public Optional<Location> findOrCreatePortal(Location targetLocation) {
        final Optional<Location> optLoc = this.findPortal(targetLocation);
        return optLoc.isPresent() ? optLoc : this.createPortal(targetLocation);
    }

    @Override
    public PortalAgentType getType() {
        return this.portalAgentType;
    }
}
