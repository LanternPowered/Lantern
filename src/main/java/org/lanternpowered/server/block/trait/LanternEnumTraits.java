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
package org.lanternpowered.server.block.trait;

import org.lanternpowered.server.data.type.LanternBedPart;
import org.lanternpowered.server.data.type.LanternBigMushroomType;
import org.lanternpowered.server.data.type.LanternBrickType;
import org.lanternpowered.server.data.type.LanternDirtType;
import org.lanternpowered.server.data.type.LanternDisguisedBlockType;
import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.data.type.LanternLogAxis;
import org.lanternpowered.server.data.type.LanternPlantType;
import org.lanternpowered.server.data.type.LanternPortionType;
import org.lanternpowered.server.data.type.LanternPrismarineType;
import org.lanternpowered.server.data.type.LanternQuartzType;
import org.lanternpowered.server.data.type.LanternRailDirection;
import org.lanternpowered.server.data.type.LanternSandType;
import org.lanternpowered.server.data.type.LanternSandstoneType;
import org.lanternpowered.server.data.type.LanternShrubType;
import org.lanternpowered.server.data.type.LanternSlabType;
import org.lanternpowered.server.data.type.LanternStoneType;
import org.lanternpowered.server.data.type.LanternTreeType;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Direction;

@SuppressWarnings("unchecked")
public final class LanternEnumTraits {

    public static final EnumTrait<LanternLogAxis> LOG_AXIS =
            LanternEnumTrait.of("axis", (Key) Keys.LOG_AXIS, LanternLogAxis.class);

    public static final EnumTrait<LanternTreeType> LOG1_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.TREE_TYPE,
                    LanternTreeType.OAK, LanternTreeType.SPRUCE, LanternTreeType.BIRCH, LanternTreeType.JUNGLE);

    public static final EnumTrait<LanternTreeType> LOG2_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.TREE_TYPE,
                    LanternTreeType.ACACIA, LanternTreeType.DARK_OAK);

    public static final EnumTrait<LanternTreeType> LEAVES1_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.TREE_TYPE,
                    LanternTreeType.OAK, LanternTreeType.SPRUCE, LanternTreeType.BIRCH, LanternTreeType.JUNGLE);

    public static final EnumTrait<LanternTreeType> LEAVES2_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.TREE_TYPE,
                    LanternTreeType.ACACIA, LanternTreeType.DARK_OAK);

    public static final EnumTrait<LanternStoneType> STONE_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.STONE_TYPE, LanternStoneType.class);

    public static final EnumTrait<LanternBrickType> BRICK_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.BRICK_TYPE, LanternBrickType.class);

    public static final EnumTrait<LanternDisguisedBlockType> DISGUISED_BLOCK_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.DISGUISED_BLOCK_TYPE, LanternDisguisedBlockType.class);

    public static final EnumTrait<LanternBigMushroomType> BIG_MUSHROOM_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.BIG_MUSHROOM_TYPE, LanternBigMushroomType.class);

    public static final EnumTrait<LanternPrismarineType> PRISMARINE_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.PRISMARINE_TYPE, LanternPrismarineType.class);

    public static final EnumTrait<LanternDirtType> DIRT_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.DIRT_TYPE, LanternDirtType.class);

    public static final EnumTrait<LanternTreeType> TREE_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.TREE_TYPE, LanternTreeType.class);

    public static final EnumTrait<LanternSandType> SAND_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.SAND_TYPE, LanternSandType.class);

    public static final EnumTrait<LanternSandstoneType> SANDSTONE_TYPE =
            LanternEnumTrait.of("type", (Key) Keys.SANDSTONE_TYPE, LanternSandstoneType.class);

    public static final EnumTrait<LanternQuartzType> QUARTZ_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.QUARTZ_TYPE, LanternQuartzType.class);

    public static final EnumTrait<LanternBedPart> BED_PART =
            LanternEnumTrait.of("type", (Key) Keys.SANDSTONE_TYPE, LanternBedPart.class);

    public static final EnumTrait<Direction> HORIZONTAL_FACING =
            LanternEnumTrait.of("facing", (Key) Keys.DIRECTION, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST);

    public static final EnumTrait<Direction> FACING =
            LanternEnumTrait.of("facing", (Key) Keys.DIRECTION, Direction.DOWN, Direction.UP,
                    Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST);

    public static final EnumTrait<LanternPortionType> PORTION_TYPE =
            LanternEnumTrait.of("half", (Key) Keys.PORTION_TYPE, LanternPortionType.class);

    public static final EnumTrait<LanternSlabType> STONE_SLAB1_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.SLAB_TYPE, LanternSlabType.class, type -> type.ordinal() >= 0 && type.ordinal() < 8);

    public static final EnumTrait<LanternSlabType> STONE_SLAB2_TYPE =
            LanternEnumTrait.of("variant", (Key) Keys.SLAB_TYPE, LanternSlabType.class, type -> type.ordinal() >= 8 && type.ordinal() < 16);

    public static final EnumTrait<Direction> HOPPER_FACING =
            LanternEnumTrait.of("variant", (Key) Keys.DIRECTION, Direction.DOWN, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST);

    public static final EnumTrait<LanternDyeColor> DYE_COLOR =
            LanternEnumTrait.of("variant", (Key) Keys.DYE_COLOR, LanternDyeColor.class);

    public static final EnumTrait<LanternRailDirection> STRAIGHT_RAIL_DIRECTION =
            LanternEnumTrait.of("shape", (Key) Keys.RAIL_DIRECTION, LanternRailDirection.class, type ->
                    type != LanternRailDirection.NORTH_EAST && type != LanternRailDirection.NORTH_WEST &&
                            type != LanternRailDirection.SOUTH_EAST && type != LanternRailDirection.SOUTH_WEST);

    public static final EnumTrait<LanternRailDirection> RAIL_DIRECTION =
            LanternEnumTrait.of("shape", (Key) Keys.RAIL_DIRECTION, LanternRailDirection.class);

    public static final EnumTrait<LanternShrubType> SHRUB_TYPE =
            LanternEnumTrait.of("type", (Key) Keys.SHRUB_TYPE, LanternShrubType.class);

    public static final EnumTrait<LanternPlantType> YELLOW_FLOWER_TYPE =
            LanternEnumTrait.of("type", (Key) Keys.PLANT_TYPE, LanternPlantType.class, type -> type.getInternalId() < 16);

    public static final EnumTrait<LanternPlantType> RED_FLOWER_TYPE =
            LanternEnumTrait.of("type", (Key) Keys.PLANT_TYPE, LanternPlantType.class, type -> type.getInternalId() >= 16);

    public static final EnumTrait<Direction> TORCH_FACING =
            LanternEnumTrait.of("facing", (Key) Keys.DIRECTION, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST, Direction.UP);

    private LanternEnumTraits() {
    }
}
