/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.world;

import java.util.Optional;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleporterAgent;
import org.spongepowered.api.world.World;

public class LanternTeleporterAgent implements TeleporterAgent {

    protected boolean canCreateTeleporter;

    protected int searchRadius;
    protected int creationRadius;

    @Override
    public int getTeleporterSearchRadius() {
        return this.searchRadius;
    }

    @Override
    public TeleporterAgent setTeleporterSearchRadius(int radius) {
        this.searchRadius = radius;
        return this;
    }

    @Override
    public int getTeleporterCreationRadius() {
        return this.creationRadius;
    }

    @Override
    public TeleporterAgent setTeleporterCreationRadius(int radius) {
        this.creationRadius = radius;
        return null;
    }

    @Override
    public boolean canCreateTeleporter() {
        return this.canCreateTeleporter;
    }

    @Override
    public TeleporterAgent setCanCreateTeleporter() {
        this.canCreateTeleporter = true;
        return this;
    }

    @Override
    public Optional<Location<World>> findOrCreateTeleporter(Location<World> targetLocation) {
        Optional<Location<World>> teleporter = this.findTeleporter(targetLocation);
        if (teleporter.isPresent()) {
            return teleporter;
        }
        return this.createTeleporter(targetLocation);
    }

    @Override
    public Optional<Location<World>> findTeleporter(Location<World> targetLocation) {
        return this.findTeleporter0(targetLocation);
    }

    @Override
    public Optional<Location<World>> createTeleporter(Location<World> targetLocation) {
        if (!this.canCreateTeleporter) {
            return Optional.empty();
        }
        return this.createTeleporter0(targetLocation);
    }

    protected Optional<Location<World>> findTeleporter0(Location<World> targetLocation) {
        return Optional.empty();
    }

    protected Optional<Location<World>> createTeleporter0(Location<World> targetLocation) {
        return Optional.empty();
    }
}
