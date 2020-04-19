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
package org.lanternpowered.server.game.registry.type.block;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.block.BlockSoundGroups;
import org.lanternpowered.server.block.LanternBlockSoundGroup;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder;
import org.lanternpowered.server.game.registry.type.effect.sound.SoundTypeRegistryModule;
import org.spongepowered.api.block.BlockSoundGroup;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RegistrationDependency(SoundTypeRegistryModule.class)
public class BlockSoundGroupRegistryModule implements RegistryModule, CatalogMappingDataHolder {

    private final Map<String, BlockSoundGroup> blockSoundGroups = new HashMap<>();

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        return ImmutableList.of(new CatalogMappingData(BlockSoundGroups.class, this.blockSoundGroups));
    }

    @Override
    public void registerDefaults() {
        register("anvil", LanternBlockSoundGroup.builder()
                .volume(0.3)
                .breakSound(SoundTypes.BLOCK_ANVIL_BREAK)
                .fallSound(SoundTypes.BLOCK_ANVIL_FALL)
                .hitSound(SoundTypes.BLOCK_ANVIL_HIT)
                .placeSound(SoundTypes.BLOCK_ANVIL_PLACE)
                .stepSound(SoundTypes.BLOCK_ANVIL_STEP)
                .build());
        register("wool", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_WOOD_BREAK)
                .fallSound(SoundTypes.BLOCK_WOOL_FALL)
                .hitSound(SoundTypes.BLOCK_WOOL_HIT)
                .placeSound(SoundTypes.BLOCK_WOOL_PLACE)
                .stepSound(SoundTypes.BLOCK_WOOL_STEP)
                .build());
        register("glass", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_GLASS_BREAK)
                .fallSound(SoundTypes.BLOCK_GLASS_FALL)
                .hitSound(SoundTypes.BLOCK_GLASS_HIT)
                .placeSound(SoundTypes.BLOCK_GLASS_PLACE)
                .stepSound(SoundTypes.BLOCK_GLASS_STEP)
                .build());
        register("gravel", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_GRAVEL_BREAK)
                .fallSound(SoundTypes.BLOCK_GRAVEL_FALL)
                .hitSound(SoundTypes.BLOCK_GRAVEL_HIT)
                .placeSound(SoundTypes.BLOCK_GRAVEL_PLACE)
                .stepSound(SoundTypes.BLOCK_GRAVEL_STEP)
                .build());
        register("ladder", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_LADDER_BREAK)
                .fallSound(SoundTypes.BLOCK_LADDER_FALL)
                .hitSound(SoundTypes.BLOCK_LADDER_HIT)
                .placeSound(SoundTypes.BLOCK_LADDER_PLACE)
                .stepSound(SoundTypes.BLOCK_LADDER_STEP)
                .build());
        register("metal", LanternBlockSoundGroup.builder()
                .pitch(1.5)
                .breakSound(SoundTypes.BLOCK_METAL_BREAK)
                .fallSound(SoundTypes.BLOCK_METAL_FALL)
                .hitSound(SoundTypes.BLOCK_METAL_HIT)
                .placeSound(SoundTypes.BLOCK_METAL_PLACE)
                .stepSound(SoundTypes.BLOCK_METAL_STEP)
                .build());
        register("grass", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_GRASS_BREAK)
                .fallSound(SoundTypes.BLOCK_GRASS_FALL)
                .hitSound(SoundTypes.BLOCK_GRASS_HIT)
                .placeSound(SoundTypes.BLOCK_GRASS_PLACE)
                .stepSound(SoundTypes.BLOCK_GRASS_STEP)
                .build());
        register("sand", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_SAND_BREAK)
                .fallSound(SoundTypes.BLOCK_SAND_FALL)
                .hitSound(SoundTypes.BLOCK_SAND_HIT)
                .placeSound(SoundTypes.BLOCK_SAND_PLACE)
                .stepSound(SoundTypes.BLOCK_SAND_STEP)
                .build());
        register("slime", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_SLIME_BLOCK_BREAK)
                .fallSound(SoundTypes.BLOCK_SLIME_BLOCK_FALL)
                .hitSound(SoundTypes.BLOCK_SLIME_BLOCK_HIT)
                .placeSound(SoundTypes.BLOCK_SLIME_BLOCK_PLACE)
                .stepSound(SoundTypes.BLOCK_SLIME_BLOCK_STEP)
                .build());
        register("snow", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_SNOW_BREAK)
                .fallSound(SoundTypes.BLOCK_SNOW_FALL)
                .hitSound(SoundTypes.BLOCK_SNOW_HIT)
                .placeSound(SoundTypes.BLOCK_SNOW_PLACE)
                .stepSound(SoundTypes.BLOCK_SNOW_STEP)
                .build());
        register("stone", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_STONE_BREAK)
                .fallSound(SoundTypes.BLOCK_STONE_FALL)
                .hitSound(SoundTypes.BLOCK_STONE_HIT)
                .placeSound(SoundTypes.BLOCK_STONE_PLACE)
                .stepSound(SoundTypes.BLOCK_STONE_STEP)
                .build());
        register("wood", LanternBlockSoundGroup.builder()
                .breakSound(SoundTypes.BLOCK_WOOD_BREAK)
                .fallSound(SoundTypes.BLOCK_WOOD_FALL)
                .hitSound(SoundTypes.BLOCK_WOOD_HIT)
                .placeSound(SoundTypes.BLOCK_WOOD_PLACE)
                .stepSound(SoundTypes.BLOCK_WOOD_STEP)
                .build());
    }

    public void register(String mapping, BlockSoundGroup blockSoundGroup) {
        this.blockSoundGroups.put(mapping.toLowerCase(Locale.ENGLISH), blockSoundGroup);
    }
}
