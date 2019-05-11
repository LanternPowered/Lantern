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
package org.lanternpowered.server.block.state.property;

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternBedPart;
import org.lanternpowered.server.data.type.LanternChestAttachment;
import org.lanternpowered.server.data.type.LanternInstrumentType;
import org.lanternpowered.server.data.type.LanternPortionType;
import org.lanternpowered.server.data.type.LanternRailDirection;
import org.lanternpowered.server.data.type.LanternSlabPortion;
import org.lanternpowered.server.data.type.RedstoneConnectionType;
import org.lanternpowered.server.state.LanternEnumStateProperty;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.state.EnumStateProperty;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Direction;

@SuppressWarnings("unchecked")
public final class LanternEnumStateProperties {

    public static final EnumStateProperty<LanternBedPart> BED_PART =
            LanternEnumStateProperty.minecraft("type", (Key) LanternKeys.BED_PART, LanternBedPart.class);

    public static final EnumStateProperty<Direction> HORIZONTAL_FACING =
            LanternEnumStateProperty.minecraft("facing", (Key) Keys.DIRECTION,
                    Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    public static final EnumStateProperty<Direction> FACING =
            LanternEnumStateProperty.minecraft("facing", Keys.DIRECTION,
                    Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN);

    public static final EnumStateProperty<LanternPortionType> PORTION_TYPE =
            LanternEnumStateProperty.minecraft("half", (Key) Keys.PORTION_TYPE, LanternPortionType.class);

    public static final EnumStateProperty<Direction> HOPPER_FACING =
            LanternEnumStateProperty.minecraft("variant", (Key) Keys.DIRECTION,
                    Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);

    public static final EnumStateProperty<Axis> AXIS = LanternEnumStateProperty.minecraft("axis", Keys.AXIS, Axis.class);

    public static final EnumStateProperty<LanternSlabPortion> SLAB_PORTION =
            LanternEnumStateProperty.minecraft("type", (Key) Keys.SLAB_PORTION, LanternSlabPortion.class);

    public static final EnumStateProperty<LanternRailDirection> STRAIGHT_RAIL_DIRECTION =
            LanternEnumStateProperty.minecraft("shape", (Key) Keys.RAIL_DIRECTION, LanternRailDirection.class, type ->
                    type != LanternRailDirection.NORTH_EAST && type != LanternRailDirection.NORTH_WEST &&
                            type != LanternRailDirection.SOUTH_EAST && type != LanternRailDirection.SOUTH_WEST);

    public static final EnumStateProperty<LanternChestAttachment> CHEST_ATTACHMENT =
            LanternEnumStateProperty.minecraft("type", (Key) Keys.CHEST_ATTACHMENT, LanternChestAttachment.class);

    public static final EnumStateProperty<LanternRailDirection> RAIL_DIRECTION =
            LanternEnumStateProperty.minecraft("shape", (Key) Keys.RAIL_DIRECTION, LanternRailDirection.class);

    public static final EnumStateProperty<Direction> TORCH_FACING =
            LanternEnumStateProperty.minecraft("facing", (Key) Keys.DIRECTION,
                    Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST, Direction.UP);

    public static final EnumStateProperty<RedstoneConnectionType> REDSTONE_NORTH_CONNECTION =
            LanternEnumStateProperty.minecraft("north", (Key) LanternKeys.REDSTONE_NORTH_CONNECTION, RedstoneConnectionType.class);

    public static final EnumStateProperty<RedstoneConnectionType> REDSTONE_SOUTH_CONNECTION =
            LanternEnumStateProperty.minecraft("south", (Key) LanternKeys.REDSTONE_SOUTH_CONNECTION, RedstoneConnectionType.class);

    public static final EnumStateProperty<RedstoneConnectionType> REDSTONE_EAST_CONNECTION =
            LanternEnumStateProperty.minecraft("east", (Key) LanternKeys.REDSTONE_EAST_CONNECTION, RedstoneConnectionType.class);

    public static final EnumStateProperty<RedstoneConnectionType> REDSTONE_WEST_CONNECTION =
            LanternEnumStateProperty.minecraft("west", (Key) LanternKeys.REDSTONE_WEST_CONNECTION, RedstoneConnectionType.class);

    public static final EnumStateProperty<LanternInstrumentType> INSTRUMENT =
            LanternEnumStateProperty.minecraft("instrument", (Key) LanternKeys.INSTRUMENT_TYPE, LanternInstrumentType.class);

    private LanternEnumStateProperties() {
    }
}
