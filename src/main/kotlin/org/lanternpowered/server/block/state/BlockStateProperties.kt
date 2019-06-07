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
package org.lanternpowered.server.block.state

import org.lanternpowered.api.catalog.CatalogKeys.minecraft
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.data.type.LanternChestAttachment
import org.lanternpowered.server.data.type.LanternInstrumentType
import org.lanternpowered.server.data.type.LanternNotePitch
import org.lanternpowered.server.data.type.LanternPortionType
import org.lanternpowered.server.data.type.LanternRailDirection
import org.lanternpowered.server.data.type.LanternSlabPortion
import org.lanternpowered.server.data.type.RedstoneConnectionType
import org.lanternpowered.server.game.registry.type.data.NotePitchRegistryModule
import org.lanternpowered.server.state.property.booleanStatePropertyOf
import org.lanternpowered.server.state.property.enumStatePropertyOf
import org.lanternpowered.server.state.property.intStatePropertyOf
import org.lanternpowered.server.state.stateKeyValueTransformer
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.util.Direction

private typealias K<V> = Key<Value<V>>

@Suppress("UNCHECKED_CAST")
object BlockStateProperties {

    // Boolean state properties

    @JvmField val SNOWY = booleanStatePropertyOf(minecraft("snowy"), Keys.SNOWED)

    @JvmField val PERSISTENT = booleanStatePropertyOf(minecraft("persistent"), Keys.PERSISTENT)

    @JvmField val IS_WET = booleanStatePropertyOf(minecraft("wet"), Keys.IS_WET)

    @JvmField val OCCUPIED = booleanStatePropertyOf(minecraft("occupied"), Keys.OCCUPIED)

    @JvmField val ENABLED = booleanStatePropertyOf(minecraft("enabled"), LanternKeys.ENABLED)

    @JvmField val TRIGGERED = booleanStatePropertyOf(minecraft("triggered"), LanternKeys.TRIGGERED)

    @JvmField val POWERED = booleanStatePropertyOf(minecraft("powered"), Keys.POWERED)

    @JvmField val EXPLODE = booleanStatePropertyOf(minecraft("explode"), LanternKeys.EXPLODE)

    @JvmField val HAS_MUSIC_DISC = booleanStatePropertyOf(minecraft("has_record"), LanternKeys.HAS_MUSIC_DISC)

    @JvmField val LIT = booleanStatePropertyOf(minecraft("lit"), Keys.LIT)

    @JvmField val WATERLOGGED = booleanStatePropertyOf(minecraft("waterlogged"), LanternKeys.WATERLOGGED)

    @JvmField val CONNECTED_NORTH = booleanStatePropertyOf(minecraft("north"), LanternKeys.CONNECTED_NORTH)

    @JvmField val CONNECTED_SOUTH = booleanStatePropertyOf(minecraft("south"), LanternKeys.CONNECTED_SOUTH)

    @JvmField val CONNECTED_EAST = booleanStatePropertyOf(minecraft("east"), LanternKeys.CONNECTED_EAST)

    @JvmField val CONNECTED_WEST = booleanStatePropertyOf(minecraft("west"), LanternKeys.CONNECTED_WEST)

    // Enum state properties

    @JvmField val BED_PART = enumStatePropertyOf(minecraft("type"), LanternKeys.BED_PART)

    @JvmField val HORIZONTAL_FACING = enumStatePropertyOf(minecraft("facing"), Keys.DIRECTION,
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)

    @JvmField val FACING = enumStatePropertyOf(minecraft("facing"), Keys.DIRECTION,
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN)

    @JvmField val PORTION_TYPE = enumStatePropertyOf(minecraft("half"),
            Keys.PORTION_TYPE as K<LanternPortionType>)

    @JvmField val HOPPER_FACING = enumStatePropertyOf(minecraft("variant"), Keys.DIRECTION,
            Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)

    @JvmField val AXIS = enumStatePropertyOf(minecraft("axis"), Keys.AXIS)

    @JvmField val SLAB_PORTION = enumStatePropertyOf(minecraft("type"),
            Keys.SLAB_PORTION as K<LanternSlabPortion>)

    @JvmField val STRAIGHT_RAIL_DIRECTION = enumStatePropertyOf(minecraft("shape"), Keys.RAIL_DIRECTION as K<LanternRailDirection>,
            LanternRailDirection.NORTH_SOUTH,
            LanternRailDirection.EAST_WEST,
            LanternRailDirection.ASCENDING_EAST,
            LanternRailDirection.ASCENDING_WEST,
            LanternRailDirection.ASCENDING_NORTH,
            LanternRailDirection.ASCENDING_SOUTH)

    @JvmField val CHEST_ATTACHMENT = enumStatePropertyOf(minecraft("type"),
            Keys.CHEST_ATTACHMENT as K<LanternChestAttachment>)

    @JvmField val RAIL_DIRECTION = enumStatePropertyOf(minecraft("shape"),
            Keys.RAIL_DIRECTION as K<LanternRailDirection>)

    @JvmField val TORCH_FACING = enumStatePropertyOf(minecraft("facing"), Keys.DIRECTION,
            Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST, Direction.UP)

    @JvmField val REDSTONE_NORTH_CONNECTION = enumStatePropertyOf(minecraft("north"),
            LanternKeys.REDSTONE_NORTH_CONNECTION as K<RedstoneConnectionType>)

    @JvmField val REDSTONE_SOUTH_CONNECTION = enumStatePropertyOf(minecraft("south"),
            LanternKeys.REDSTONE_SOUTH_CONNECTION as K<RedstoneConnectionType>)

    @JvmField val REDSTONE_EAST_CONNECTION = enumStatePropertyOf(minecraft("east"),
            LanternKeys.REDSTONE_EAST_CONNECTION as K<RedstoneConnectionType>)

    @JvmField val REDSTONE_WEST_CONNECTION = enumStatePropertyOf(minecraft("west"),
            LanternKeys.REDSTONE_WEST_CONNECTION as K<RedstoneConnectionType>)

    @JvmField val INSTRUMENT = enumStatePropertyOf(minecraft("instrument"),
            LanternKeys.INSTRUMENT_TYPE as K<LanternInstrumentType>)

    // Int state properties

    @JvmField val SAPLING_GROWTH_STAGE = intStatePropertyOf(minecraft("stage"), Keys.GROWTH_STAGE, 0..1)

    @JvmField val POWER = intStatePropertyOf(minecraft("power"), Keys.POWER, 0..15)

    @JvmField val MOISTURE = intStatePropertyOf(minecraft("moisture"), Keys.MOISTURE, 0..7)

    @JvmField val ROTATION = intStatePropertyOf(minecraft("rotation"), LanternKeys.FINE_ROTATION, 0..15)

    @JvmField val NOTE = intStatePropertyOf(minecraft("note"), Keys.NOTE_PITCH, 0..24,
            stateKeyValueTransformer(
                    { stateValue -> NotePitchRegistryModule.getByInternalId(stateValue).get() },
                    { keyValue -> (keyValue as LanternNotePitch).internalId }))

    @JvmField val DECAY_DISTANCE = intStatePropertyOf(minecraft("distance"), Keys.DECAY_DISTANCE, 1..7)
}
