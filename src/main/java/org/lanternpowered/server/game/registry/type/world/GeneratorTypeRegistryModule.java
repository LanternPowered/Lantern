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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.game.registry.util.RegistryHelper;
import org.lanternpowered.server.world.gen.LanternGeneratorTypeNether;
import org.lanternpowered.server.world.gen.debug.DebugGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatGeneratorType;
import org.lanternpowered.server.world.gen.skylands.SkylandsGeneratorType;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class GeneratorTypeRegistryModule implements CatalogRegistryModule<GeneratorType> {

    private final Map<String, GeneratorType> generatorTypes = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        List<GeneratorType> types = Lists.newArrayList();
        types.add(new LanternGeneratorTypeNether("minecraft", "nether"));
        types.add(new FlatGeneratorType("minecraft", "flat"));
        types.add(new DebugGeneratorType("minecraft", "debug"));
        types.add(new SkylandsGeneratorType("sponge", "skylands"));

        Map<String, GeneratorType> mappings = Maps.newHashMap();
        for (GeneratorType type : types) {
            this.generatorTypes.put(type.getId(), type);
            mappings.put(type.getName(), type);
        }
        RegistryHelper.mapFields(GeneratorTypes.class, mappings);
    }

    @Override
    public Optional<GeneratorType> getById(String id) {
        if (checkNotNull(id).indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.generatorTypes.get(id.toLowerCase()));
    }

    @Override
    public Collection<GeneratorType> getAll() {
        return ImmutableSet.copyOf(this.generatorTypes.values());
    }

}
