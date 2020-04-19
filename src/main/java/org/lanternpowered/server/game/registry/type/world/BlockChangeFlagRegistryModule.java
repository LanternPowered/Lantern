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
package org.lanternpowered.server.game.registry.type.world;

import static org.lanternpowered.server.world.LanternBlockChangeFlag.ALL;
import static org.lanternpowered.server.world.LanternBlockChangeFlag.NEIGHBOR;
import static org.lanternpowered.server.world.LanternBlockChangeFlag.NONE;
import static org.lanternpowered.server.world.LanternBlockChangeFlag.OBSERVER;
import static org.lanternpowered.server.world.LanternBlockChangeFlag.PHYSICS;

import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.BlockChangeFlags;

import java.util.HashMap;
import java.util.Map;

public final class BlockChangeFlagRegistryModule implements RegistryModule {

    @RegisterCatalog(BlockChangeFlags.class) private final Map<String, BlockChangeFlag> mappings = new HashMap<>();

    @Override
    public void registerDefaults() {
        map("all", ALL);
        map("neighbor", NEIGHBOR);
        map("neighbor_observer", NEIGHBOR.andFlag(OBSERVER));
        map("neighbor_physics", NEIGHBOR.andFlag(PHYSICS));
        map("neighbor_physics_observer", NEIGHBOR.andFlag(PHYSICS).andFlag(OBSERVER));
        map("none", NONE);
        map("observer", OBSERVER);
        map("physics", PHYSICS);
        map("physics_observer", PHYSICS.andFlag(OBSERVER));
    }

    private void map(String id, BlockChangeFlag value) {
        this.mappings.put(id, value);
    }
}
