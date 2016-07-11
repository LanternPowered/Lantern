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
import org.lanternpowered.server.cause.entity.teleport.LanternTeleportType;
import org.spongepowered.api.event.cause.entity.teleport.TeleportType;
import org.spongepowered.api.event.cause.entity.teleport.TeleportTypes;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class TeleportTypeRegistryModule implements AdditionalCatalogRegistryModule<TeleportType>,
        AlternateCatalogRegistryModule<TeleportType> {

    @RegisterCatalog(TeleportTypes.class)
    private final Map<String, TeleportType> teleportTypes = new HashMap<>();

    @Override
    public Map<String, TeleportType> provideCatalogMap() {
        Map<String, TeleportType> provided = new HashMap<>();
        for (Map.Entry<String, TeleportType> entry : this.teleportTypes.entrySet()) {
            provided.put(entry.getKey().replace("minecraft:", ""), entry.getValue());
        }
        return provided;
    }

    @Override
    public void registerAdditionalCatalog(TeleportType teleportType) {
        checkNotNull(teleportType, "teleportType");
        final String id = teleportType.getId();
        validateIdentifier(id);
        checkState(!this.teleportTypes.containsKey(id),
                "There is already a teleport type registered with the id. (" + id + ")");
        this.teleportTypes.put(id, teleportType);
    }

    @Override
    public void registerDefaults() {
        this.registerAdditionalCatalog(new LanternTeleportType("minecraft", "command"));
        this.registerAdditionalCatalog(new LanternTeleportType("minecraft", "entity_teleport"));
        this.registerAdditionalCatalog(new LanternTeleportType("minecraft", "plugin"));
        this.registerAdditionalCatalog(new LanternTeleportType("minecraft", "portal"));
        this.registerAdditionalCatalog(new LanternTeleportType("minecraft", "unknown"));
    }

    @Override
    public Optional<TeleportType> getById(String id) {
        if (checkNotNull(id).indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.teleportTypes.get(id.toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<TeleportType> getAll() {
        return ImmutableSet.copyOf(this.teleportTypes.values());
    }

}
