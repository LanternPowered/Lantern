/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.world.dimension.LanternDimensionEnd;
import org.lanternpowered.server.world.dimension.LanternDimensionNether;
import org.lanternpowered.server.world.dimension.LanternDimensionOverworld;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency(GeneratorTypeRegistryModule.class)
public class DimensionTypeRegistryModule implements AlternateCatalogRegistryModule<DimensionType> {

    @RegisterCatalog(DimensionTypes.class) private final Map<String, DimensionType> dimensionTypes = Maps.newHashMap();

    @Override
    public Map<String, DimensionType> provideCatalogMap() {
        Map<String, DimensionType> mappings = Maps.newHashMap();
        for (DimensionType type : this.dimensionTypes.values()) {
            mappings.put(type.getName(), type);
        }
        return mappings;
    }

    @Override
    public void registerDefaults() {
        List<DimensionType> types = Lists.newArrayList();
        types.add(new LanternDimensionType<>("minecraft", "the_end", -1, LanternDimensionEnd.class, GeneratorTypes.THE_END, true, false, false,
                false, (world, type) -> new LanternDimensionEnd(world, type.getName(), type)));
        types.add(new LanternDimensionType<>("minecraft", "overworld", 0, LanternDimensionOverworld.class, GeneratorTypes.OVERWORLD, true, false,
                false, true, (world, type) -> new LanternDimensionOverworld(world, type.getName(), type)));
        types.add(new LanternDimensionType<>("minecraft", "nether", 1, LanternDimensionNether.class, GeneratorTypes.NETHER, true, false, false,
                false, (world, type) -> new LanternDimensionNether(world, type.getName(), type)));

        for (DimensionType type : types) {
            this.dimensionTypes.put(type.getId(), type);
        }
    }

    @Override
    public Optional<DimensionType> getById(String id) {
        if (checkNotNull(id).indexOf(':') == -1) {
            id = "minecraft:" + id;
        }
        return Optional.ofNullable(this.dimensionTypes.get(id.toLowerCase()));
    }

    @Override
    public Collection<DimensionType> getAll() {
        return ImmutableList.copyOf(this.dimensionTypes.values());
    }

}
