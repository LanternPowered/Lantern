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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.spongepowered.api.extra.skylands.SkylandsWorldGeneratorModifier;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.gen.WorldGeneratorModifiers;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class GeneratorModifierRegistryModule implements AdditionalCatalogRegistryModule<WorldGeneratorModifier>,
        AlternateCatalogRegistryModule<WorldGeneratorModifier> {

    @RegisterCatalog(WorldGeneratorModifiers.class) private final Map<String, WorldGeneratorModifier> generatorModifiers = Maps.newHashMap();

    @Override
    public Map<String, WorldGeneratorModifier> provideCatalogMap() {
        Map<String, WorldGeneratorModifier> mappings = Maps.newHashMap();
        for (WorldGeneratorModifier type : this.generatorModifiers.values()) {
            mappings.put(type.getName(), type);
        }
        return mappings;
    }

    @Override
    public void registerAdditionalCatalog(WorldGeneratorModifier modifier) {
        checkNotNull(modifier, "modifier");
        final String id = modifier.getId();
        checkNotNull(id, "identifier");
        checkArgument(id.indexOf(' ') == -1, "modifier identifier (" + id + ") may not contain a space");
        this.generatorModifiers.put(id, modifier);
    }

    @Override
    public void registerDefaults() {
        this.registerAdditionalCatalog(new SkylandsWorldGeneratorModifier());
    }

    @Override
    public Optional<WorldGeneratorModifier> getById(String id) {
        if (checkNotNull(id).indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.generatorModifiers.get(id.toLowerCase()));
    }

    @Override
    public Collection<WorldGeneratorModifier> getAll() {
        return ImmutableSet.copyOf(this.generatorModifiers.values());
    }

}
