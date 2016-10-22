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
package org.lanternpowered.server.game.registry.type.cause;

import org.lanternpowered.server.cause.entity.spawn.LanternSpawnType;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;

public class SpawnTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<SpawnType> {

    public SpawnTypeRegistryModule() {
        super(SpawnTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternSpawnType("minecraft", "block_spawning"));
        register(new LanternSpawnType("minecraft", "breeding"));
        register(new LanternSpawnType("minecraft", "chunk_load"));
        register(new LanternSpawnType("minecraft", "custom"));
        register(new LanternSpawnType("minecraft", "dispense"));
        register(new LanternSpawnType("minecraft", "dropped_item"));
        register(new LanternSpawnType("minecraft", "experience"));
        register(new LanternSpawnType("minecraft", "falling_block"));
        register(new LanternSpawnType("minecraft", "mob_spawner"));
        register(new LanternSpawnType("minecraft", "passive"));
        register(new LanternSpawnType("minecraft", "placement"));
        register(new LanternSpawnType("minecraft", "plugin"));
        register(new LanternSpawnType("minecraft", "projectile"));
        register(new LanternSpawnType("minecraft", "spawn_egg"));
        register(new LanternSpawnType("minecraft", "structure"));
        register(new LanternSpawnType("minecraft", "tnt_ignite"));
        register(new LanternSpawnType("minecraft", "weather"));
        register(new LanternSpawnType("minecraft", "world_spawner"));
    }
}
