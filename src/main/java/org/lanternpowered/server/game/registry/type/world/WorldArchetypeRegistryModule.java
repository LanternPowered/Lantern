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

import org.lanternpowered.server.game.registry.type.entity.player.GameModeRegistryModule;
import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.SerializationBehaviors;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.gen.WorldGeneratorModifiers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency({ GameModeRegistryModule.class, GeneratorTypeRegistryModule.class, DifficultyRegistryModule.class,
        DimensionTypeRegistryModule.class, SerializationBehaviorRegistryModule.class, GeneratorModifierRegistryModule.class,
        BiomeRegistryModule.class })
public final class WorldArchetypeRegistryModule implements AdditionalCatalogRegistryModule<WorldArchetype>,
        AlternateCatalogRegistryModule<WorldArchetype> {

    @RegisterCatalog(WorldArchetypes.class)
    private final Map<String, WorldArchetype> worldArchetypes = new HashMap<>();

    @Override
    public Map<String, WorldArchetype> provideCatalogMap() {
        Map<String, WorldArchetype> provided = new HashMap<>();
        for (Map.Entry<String, WorldArchetype> entry : this.worldArchetypes.entrySet()) {
            provided.put(entry.getKey().replace("minecraft:", "").replace("sponge:", ""), entry.getValue());
        }
        return provided;
    }

    @Override
    public void registerDefaults() {
        final WorldArchetype overworld = WorldArchetype.builder()
                .enabled(true)
                .loadsOnStartup(true)
                .keepsSpawnLoaded(true)
                .generateSpawnOnLoad(true)
                .commandsAllowed(true)
                .gameMode(GameModes.SURVIVAL)
                .generator(GeneratorTypes.DEFAULT)
                .dimension(DimensionTypes.OVERWORLD)
                .difficulty(Difficulties.NORMAL)
                .usesMapFeatures(true)
                .hardcore(false)
                .pvp(true)
                .generateBonusChest(false)
                .serializationBehavior(SerializationBehaviors.AUTOMATIC)
                .build("minecraft:overworld", "Overworld");
        this.worldArchetypes.put("minecraft:overworld", overworld);
        this.worldArchetypes.put("minecraft:the_nether", WorldArchetype.builder()
                .from(overworld)
                .generator(GeneratorTypes.NETHER)
                .dimension(DimensionTypes.NETHER)
                .build("minecraft:the_nether", "The Nether"));
        this.worldArchetypes.put("minecraft:the_end", WorldArchetype.builder()
                .from(overworld)
                .generator(GeneratorTypes.THE_END)
                .dimension(DimensionTypes.THE_END)
                .build("minecraft:the_end", "The End"));
        this.worldArchetypes.put("sponge:skylands", WorldArchetype.builder()
                .from(overworld)
                .generatorModifiers(WorldGeneratorModifiers.SKYLANDS)
                .build("sponge:the_skylands", "The Skylands"));
        this.worldArchetypes.put("sponge:the_void", WorldArchetype.builder()
                .from(overworld)
                .generatorModifiers(WorldGeneratorModifiers.VOID)
                .build("sponge:the_void", "The Void"));
    }

    @Override
    public void registerAdditionalCatalog(WorldArchetype extraCatalog) {
        checkNotNull(extraCatalog, "WorldCreationSettings cannot be null!");
        final String id = extraCatalog.getId().toLowerCase(Locale.ENGLISH);
        checkArgument(!id.isEmpty(), "Id may not be empty!");
        checkArgument(!id.startsWith("minecraft:"), "Plugin trying to register a fake minecraft generation settings!");
        checkArgument(!id.startsWith("sponge:"), "Plugin trying to register a fake sponge generation settings!");
        this.worldArchetypes.put(id, extraCatalog);
    }

    @Override
    public Optional<WorldArchetype> getById(String id) {
        return Optional.ofNullable(this.worldArchetypes.get(checkNotNull(id, "id").toLowerCase(Locale.ENGLISH)));
    }

    @Override
    public Collection<WorldArchetype> getAll() {
        return Collections.unmodifiableCollection(this.worldArchetypes.values());
    }
}
