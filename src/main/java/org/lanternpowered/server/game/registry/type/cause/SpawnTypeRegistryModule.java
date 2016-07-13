/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
        this.register(new LanternSpawnType("minecraft", "block_spawning"));
        this.register(new LanternSpawnType("minecraft", "breeding"));
        this.register(new LanternSpawnType("minecraft", "chunk_load"));
        this.register(new LanternSpawnType("minecraft", "custom"));
        this.register(new LanternSpawnType("minecraft", "dispense"));
        this.register(new LanternSpawnType("minecraft", "dropped_item"));
        this.register(new LanternSpawnType("minecraft", "experience"));
        this.register(new LanternSpawnType("minecraft", "falling_block"));
        this.register(new LanternSpawnType("minecraft", "mob_spawner"));
        this.register(new LanternSpawnType("minecraft", "passive"));
        this.register(new LanternSpawnType("minecraft", "placement"));
        this.register(new LanternSpawnType("minecraft", "plugin"));
        this.register(new LanternSpawnType("minecraft", "projectile"));
        this.register(new LanternSpawnType("minecraft", "spawn_egg"));
        this.register(new LanternSpawnType("minecraft", "structure"));
        this.register(new LanternSpawnType("minecraft", "tnt_ignite"));
        this.register(new LanternSpawnType("minecraft", "weather"));
        this.register(new LanternSpawnType("minecraft", "world_spawner"));
    }
}
