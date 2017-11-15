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

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternBedPart;
import org.lanternpowered.server.data.type.LanternChestAttachment;
import org.lanternpowered.server.data.type.LanternInstrumentType;
import org.lanternpowered.server.data.type.LanternPortionType;
import org.lanternpowered.server.data.type.LanternRailDirection;
import org.lanternpowered.server.data.type.LanternSlabPortion;
import org.lanternpowered.server.data.type.RedstoneConnectionType;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Direction;

@SuppressWarnings("unchecked")
public final class LanternEnumTraits {

    public static final EnumTrait<LanternBedPart> BED_PART =
            LanternEnumTrait.minecraft("type", (Key) LanternKeys.BED_PART, LanternBedPart.class);

    public static final EnumTrait<Direction> HORIZONTAL_FACING =
            LanternEnumTrait.minecraft("facing", (Key) Keys.DIRECTION,
                    Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    public static final EnumTrait<Direction> FACING =
            LanternEnumTrait.minecraft("facing", Keys.DIRECTION,
                    Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);

    public static final EnumTrait<LanternPortionType> PORTION_TYPE =
            LanternEnumTrait.minecraft("half", (Key) Keys.PORTION_TYPE, LanternPortionType.class);

    public static final EnumTrait<Direction> HOPPER_FACING =
            LanternEnumTrait.minecraft("variant", (Key) Keys.DIRECTION,
                    Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    public static final EnumTrait<Axis> AXIS = LanternEnumTrait.minecraft("axis", Keys.AXIS, Axis.class);

    public static final EnumTrait<LanternSlabPortion> SLAB_PORTION =
            LanternEnumTrait.minecraft("type", (Key) Keys.SLAB_PORTION, LanternSlabPortion.class);

    public static final EnumTrait<LanternRailDirection> STRAIGHT_RAIL_DIRECTION =
            LanternEnumTrait.minecraft("shape", (Key) Keys.RAIL_DIRECTION, LanternRailDirection.class, type ->
                    type != LanternRailDirection.NORTH_EAST && type != LanternRailDirection.NORTH_WEST &&
                            type != LanternRailDirection.SOUTH_EAST && type != LanternRailDirection.SOUTH_WEST);

    public static final EnumTrait<LanternChestAttachment> CHEST_ATTACHMENT =
            LanternEnumTrait.minecraft("type", (Key) Keys.CHEST_ATTACHMENT, LanternChestAttachment.class);

    public static final EnumTrait<LanternRailDirection> RAIL_DIRECTION =
            LanternEnumTrait.minecraft("shape", (Key) Keys.RAIL_DIRECTION, LanternRailDirection.class);

    public static final EnumTrait<Direction> TORCH_FACING =
            LanternEnumTrait.minecraft("facing", (Key) Keys.DIRECTION, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST, Direction.UP);

    public static final EnumTrait<RedstoneConnectionType> REDSTONE_NORTH_CONNECTION =
            LanternEnumTrait.minecraft("north", (Key) LanternKeys.REDSTONE_NORTH_CONNECTION, RedstoneConnectionType.class);

    public static final EnumTrait<RedstoneConnectionType> REDSTONE_SOUTH_CONNECTION =
            LanternEnumTrait.minecraft("south", (Key) LanternKeys.REDSTONE_SOUTH_CONNECTION, RedstoneConnectionType.class);

    public static final EnumTrait<RedstoneConnectionType> REDSTONE_EAST_CONNECTION =
            LanternEnumTrait.minecraft("east", (Key) LanternKeys.REDSTONE_EAST_CONNECTION, RedstoneConnectionType.class);

    public static final EnumTrait<RedstoneConnectionType> REDSTONE_WEST_CONNECTION =
            LanternEnumTrait.minecraft("west", (Key) LanternKeys.REDSTONE_WEST_CONNECTION, RedstoneConnectionType.class);

    public static final EnumTrait<LanternInstrumentType> INSTRUMENT =
            LanternEnumTrait.minecraft("instrument", (Key) LanternKeys.INSTRUMENT_TYPE, LanternInstrumentType.class);

    private LanternEnumTraits() {
    }
}
