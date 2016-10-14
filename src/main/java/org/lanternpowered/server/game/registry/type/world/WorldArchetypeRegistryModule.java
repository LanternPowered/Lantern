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

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.entity.player.GameModeRegistryModule;
import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.SerializationBehaviors;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.WorldArchetypes;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.gen.WorldGeneratorModifiers;

@RegistrationDependency({ GameModeRegistryModule.class, GeneratorTypeRegistryModule.class, DifficultyRegistryModule.class,
        DimensionTypeRegistryModule.class, SerializationBehaviorRegistryModule.class, GeneratorModifierRegistryModule.class,
        BiomeRegistryModule.class, PortalAgentTypeRegistryModule.class })
public final class WorldArchetypeRegistryModule extends AdditionalPluginCatalogRegistryModule<WorldArchetype> {

    public WorldArchetypeRegistryModule() {
        super(WorldArchetypes.class);
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
        this.register(overworld);
        this.register(WorldArchetype.builder()
                .from(overworld)
                .generator(GeneratorTypes.NETHER)
                .dimension(DimensionTypes.NETHER)
                .build("minecraft:the_nether", "The Nether"));
        this.register(WorldArchetype.builder()
                .from(overworld)
                .generator(GeneratorTypes.THE_END)
                .dimension(DimensionTypes.THE_END)
                .build("minecraft:the_end", "The End"));
        this.register(WorldArchetype.builder()
                .from(overworld)
                .generatorModifiers(WorldGeneratorModifiers.SKYLANDS)
                .build("sponge:the_skylands", "The Skylands"));
        this.register(WorldArchetype.builder()
                .from(overworld)
                .generatorModifiers(WorldGeneratorModifiers.VOID)
                .build("sponge:the_void", "The Void"));
    }
}
