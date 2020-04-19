/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
        register(overworld);
        register(WorldArchetype.builder()
                .from(overworld)
                .generator(GeneratorTypes.NETHER)
                .dimension(DimensionTypes.NETHER)
                .build("minecraft:the_nether", "The Nether"));
        register(WorldArchetype.builder()
                .from(overworld)
                .generator(GeneratorTypes.THE_END)
                .dimension(DimensionTypes.THE_END)
                .build("minecraft:the_end", "The End"));
        register(WorldArchetype.builder()
                .from(overworld)
                .generatorModifiers(WorldGeneratorModifiers.VOID)
                .build("sponge:the_void", "The Void"));
    }
}
