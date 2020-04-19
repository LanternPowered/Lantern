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
package org.lanternpowered.server.world;

import com.google.inject.Singleton;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.teleport.TeleportHelperFilter;

import java.util.Optional;

@Singleton
public class LanternTeleportHelper implements TeleportHelper {

    @Override
    public Optional<Location> getSafeLocation(Location location, int height, int width, int floorDistance,
            TeleportHelperFilter filter, TeleportHelperFilter... additionalFilters) {
        return Optional.empty();
    }
}
