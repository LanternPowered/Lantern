package org.lanternpowered.server.world;

import java.util.Optional;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

public class LanternTeleportHelper implements TeleportHelper {

    @Override
    public Optional<Location<World>> getSafeLocation(Location<World> location) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public Optional<Location<World>> getSafeLocation(Location<World> location, int height, int width) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }
}
