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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.game.registry.RegistryModuleHelper.validateIdentifier;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.cause.entity.spawn.LanternSpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class SpawnTypeRegistryModule implements AdditionalCatalogRegistryModule<SpawnType>,
        AlternateCatalogRegistryModule<SpawnType> {

    @RegisterCatalog(SpawnTypes.class)
    private final Map<String, SpawnType> spawnTypes = new HashMap<>();

    @Override
    public Map<String, SpawnType> provideCatalogMap() {
        Map<String, SpawnType> provided = new HashMap<>();
        for (Map.Entry<String, SpawnType> entry : this.spawnTypes.entrySet()) {
            provided.put(entry.getKey().replace("minecraft:", ""), entry.getValue());
        }
        return provided;
    }

    @Override
    public void registerAdditionalCatalog(SpawnType spawnType) {
        checkNotNull(spawnType, "spawnType");
        final String id = spawnType.getId();
        validateIdentifier(id);
        checkState(!this.spawnTypes.containsKey(id),
                "There is already a spawn type registered with the id. (" + id + ")");
        this.spawnTypes.put(id, spawnType);
    }

    @Override
    public void registerDefaults() {
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "block_spawning"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "breeding"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "chunk_load"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "custom"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "dispense"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "dropped_item"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "experience"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "falling_block"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "mob_spawner"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "passive"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "placement"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "plugin"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "projectile"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "spawn_egg"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "structure"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "tnt_ignite"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "weather"));
        this.registerAdditionalCatalog(new LanternSpawnType("minecraft", "world_spawner"));
    }

    @Override
    public Optional<SpawnType> getById(String id) {
        if (checkNotNull(id).indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.spawnTypes.get(id.toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<SpawnType> getAll() {
        return ImmutableSet.copyOf(this.spawnTypes.values());
    }

}
