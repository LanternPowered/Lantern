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
