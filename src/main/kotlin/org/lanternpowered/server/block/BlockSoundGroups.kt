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
package org.lanternpowered.server.block

import org.spongepowered.api.effect.sound.SoundTypes

object BlockSoundGroups {

    val ANVIL = blockSoundGroupOf(volume = 0.3,
            breakSound = SoundTypes.BLOCK_ANVIL_BREAK,
            fallSound = SoundTypes.BLOCK_ANVIL_FALL,
            hitSound = SoundTypes.BLOCK_ANVIL_HIT,
            placeSound = SoundTypes.BLOCK_ANVIL_PLACE,
            stepSound = SoundTypes.BLOCK_ANVIL_STEP)

    val WOOL = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_WOOD_BREAK,
            fallSound = SoundTypes.BLOCK_WOOL_FALL,
            hitSound = SoundTypes.BLOCK_WOOL_HIT,
            placeSound = SoundTypes.BLOCK_WOOL_PLACE,
            stepSound = SoundTypes.BLOCK_WOOL_STEP)

    val GLASS = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_GLASS_BREAK,
            fallSound = SoundTypes.BLOCK_GLASS_FALL,
            hitSound = SoundTypes.BLOCK_GLASS_HIT,
            placeSound = SoundTypes.BLOCK_GLASS_PLACE,
            stepSound = SoundTypes.BLOCK_GLASS_STEP)

    val GRAVEL = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_GRAVEL_BREAK,
            fallSound = SoundTypes.BLOCK_GRAVEL_FALL,
            hitSound = SoundTypes.BLOCK_GRAVEL_HIT,
            placeSound = SoundTypes.BLOCK_GRAVEL_PLACE,
            stepSound = SoundTypes.BLOCK_GRAVEL_STEP)

    val LADDER = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_LADDER_BREAK,
            fallSound = SoundTypes.BLOCK_LADDER_FALL,
            hitSound = SoundTypes.BLOCK_LADDER_HIT,
            placeSound = SoundTypes.BLOCK_LADDER_PLACE,
            stepSound = SoundTypes.BLOCK_LADDER_STEP)

    val METAL = blockSoundGroupOf(pitch = 1.5,
            breakSound = SoundTypes.BLOCK_METAL_BREAK,
            fallSound = SoundTypes.BLOCK_METAL_FALL,
            hitSound = SoundTypes.BLOCK_METAL_HIT,
            placeSound = SoundTypes.BLOCK_METAL_PLACE,
            stepSound = SoundTypes.BLOCK_METAL_STEP)

    val GRASS = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_GRASS_BREAK,
            fallSound = SoundTypes.BLOCK_GRASS_FALL,
            hitSound = SoundTypes.BLOCK_GRASS_HIT,
            placeSound = SoundTypes.BLOCK_GRASS_PLACE,
            stepSound = SoundTypes.BLOCK_GRASS_STEP)

    val SAND = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_SAND_BREAK,
            fallSound = SoundTypes.BLOCK_SAND_FALL,
            hitSound = SoundTypes.BLOCK_SAND_HIT,
            placeSound = SoundTypes.BLOCK_SAND_PLACE,
            stepSound = SoundTypes.BLOCK_SAND_STEP)

    val SLIME = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_SLIME_BLOCK_BREAK,
            fallSound = SoundTypes.BLOCK_SLIME_BLOCK_FALL,
            hitSound = SoundTypes.BLOCK_SLIME_BLOCK_HIT,
            placeSound = SoundTypes.BLOCK_SLIME_BLOCK_PLACE,
            stepSound = SoundTypes.BLOCK_SLIME_BLOCK_STEP)

    val SNOW = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_SNOW_BREAK,
            fallSound = SoundTypes.BLOCK_SNOW_FALL,
            hitSound = SoundTypes.BLOCK_SNOW_HIT,
            placeSound = SoundTypes.BLOCK_SNOW_PLACE,
            stepSound = SoundTypes.BLOCK_SNOW_STEP)

    val STONE = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_STONE_BREAK,
            fallSound = SoundTypes.BLOCK_STONE_FALL,
            hitSound = SoundTypes.BLOCK_STONE_HIT,
            placeSound = SoundTypes.BLOCK_STONE_PLACE,
            stepSound = SoundTypes.BLOCK_STONE_STEP)

    val WOOD = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_WOOD_BREAK,
            fallSound = SoundTypes.BLOCK_WOOD_FALL,
            hitSound = SoundTypes.BLOCK_WOOD_HIT,
            placeSound = SoundTypes.BLOCK_WOOD_PLACE,
            stepSound = SoundTypes.BLOCK_WOOD_STEP)

    val BAMBOO = blockSoundGroupOf(
            breakSound = SoundTypes.BLOCK_BAMBOO_BREAK,
            fallSound = SoundTypes.BLOCK_BAMBOO_FALL,
            hitSound = SoundTypes.BLOCK_BAMBOO_HIT,
            placeSound = SoundTypes.BLOCK_BAMBOO_PLACE,
            stepSound = SoundTypes.BLOCK_BAMBOO_STEP)

}
