package org.lanternpowered.server.world;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;

public class LanternTeleportHelper implements TeleportHelper {

    @Override
    public Optional<Location<World>> getSafeLocation(Location<World> location) {
        // TODO Auto-generated method stub
        return Optional.absent();
    }

    @Override
    public Optional<Location<World>> getSafeLocation(Location<World> location, int height, int width) {
        // TODO Auto-generated method stub
        return Optional.absent();
    }
}
