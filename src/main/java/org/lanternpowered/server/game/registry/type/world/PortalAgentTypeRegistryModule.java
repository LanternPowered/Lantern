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
package org.lanternpowered.server.game.registry.type.world;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.world.portal.EmptyPortalAgent;
import org.lanternpowered.server.world.portal.LanternPortalAgentType;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.world.PortalAgentType;
import org.spongepowered.api.world.PortalAgentTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class PortalAgentTypeRegistryModule implements AlternateCatalogRegistryModule<PortalAgentType> {

    @RegisterCatalog(PortalAgentTypes.class) private final Map<String, PortalAgentType> portalAgentTypes = new HashMap<>();

    @Override
    public Map<String, PortalAgentType> provideCatalogMap() {
        final Map<String, PortalAgentType> mappings = new HashMap<>();
        for (PortalAgentType type : this.portalAgentTypes.values()) {
            mappings.put(type.getName(), type);
        }
        return mappings;
    }

    @Override
    public void registerDefaults() {
        final List<PortalAgentType> types = new ArrayList<>();
        types.add(new LanternPortalAgentType<>("minecraft", "default", EmptyPortalAgent.class, (world, type) -> new EmptyPortalAgent(type)));

        for (PortalAgentType type : types) {
            this.portalAgentTypes.put(type.getId(), type);
        }
    }

    @Override
    public Optional<PortalAgentType> getById(String id) {
        if (checkNotNull(id).indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.portalAgentTypes.get(id.toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<PortalAgentType> getAll() {
        return ImmutableSet.copyOf(this.portalAgentTypes.values());
    }

}
