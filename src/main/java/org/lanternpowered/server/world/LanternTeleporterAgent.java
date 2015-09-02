package org.lanternpowered.server.world;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleporterAgent;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;

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
            return Optional.absent();
        }
        return this.createTeleporter0(targetLocation);
    }

    protected Optional<Location<World>> findTeleporter0(Location<World> targetLocation) {
        return Optional.absent();
    }

    protected Optional<Location<World>> createTeleporter0(Location<World> targetLocation) {
        return Optional.absent();
    }
}
