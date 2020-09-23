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
package org.lanternpowered.server.block.state

import org.lanternpowered.api.NamespacedKeys.minecraft
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.data.type.LanternChestAttachmentType
import org.lanternpowered.server.data.type.LanternInstrumentType
import org.lanternpowered.server.data.type.LanternPortionType
import org.lanternpowered.server.data.type.LanternRailDirection
import org.lanternpowered.server.data.type.LanternSlabPortion
import org.lanternpowered.server.data.type.LanternWireAttachmentType
import org.lanternpowered.server.registry.type.data.NotePitchRegistry
import org.lanternpowered.server.state.property.booleanStatePropertyOf
import org.lanternpowered.server.state.property.enumStatePropertyOf
import org.lanternpowered.server.state.property.intStatePropertyOf
import org.lanternpowered.server.state.stateKeyValueTransformer
import org.spongepowered.api.data.Key
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.util.Direction

private typealias K<V> = Key<Value<V>>

@Suppress("UNCHECKED_CAST")
object BlockStateProperties {

    // Boolean state properties

    @JvmField val SNOWY = booleanStatePropertyOf(minecraft("snowy"), Keys.IS_SNOWY)

    @JvmField val PERSISTENT = booleanStatePropertyOf(minecraft("persistent"), Keys.IS_PERSISTENT)

    @JvmField val IS_WET = booleanStatePropertyOf(minecraft("wet"), Keys.IS_WET)

    @JvmField val OCCUPIED = booleanStatePropertyOf(minecraft("occupied"), Keys.IS_OCCUPIED)

    @JvmField val ENABLED = booleanStatePropertyOf(minecraft("enabled"), LanternKeys.ENABLED)

    @JvmField val TRIGGERED = booleanStatePropertyOf(minecraft("triggered"), LanternKeys.TRIGGERED)

    @JvmField val POWERED = booleanStatePropertyOf(minecraft("powered"), Keys.IS_POWERED)

    @JvmField val UNSTABLE = booleanStatePropertyOf(minecraft("explode"), LanternKeys.UNSTABLE)

    @JvmField val HAS_MUSIC_DISC = booleanStatePropertyOf(minecraft("has_record"), LanternKeys.HAS_MUSIC_DISC)

    @JvmField val LIT = booleanStatePropertyOf(minecraft("lit"), Keys.IS_LIT)

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

    @JvmField val CHEST_ATTACHMENT_TYPE = enumStatePropertyOf(minecraft("type"),
            Keys.CHEST_ATTACHMENT_TYPE as K<LanternChestAttachmentType>)

    @JvmField val RAIL_DIRECTION = enumStatePropertyOf(minecraft("shape"),
            Keys.RAIL_DIRECTION as K<LanternRailDirection>)

    @JvmField val TORCH_FACING = enumStatePropertyOf(minecraft("facing"), Keys.DIRECTION,
            Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST, Direction.UP)

    @JvmField val REDSTONE_NORTH_CONNECTION = enumStatePropertyOf(minecraft("north"),
            LanternKeys.REDSTONE_NORTH_CONNECTION as K<LanternWireAttachmentType>)

    @JvmField val REDSTONE_SOUTH_CONNECTION = enumStatePropertyOf(minecraft("south"),
            LanternKeys.REDSTONE_SOUTH_CONNECTION as K<LanternWireAttachmentType>)

    @JvmField val REDSTONE_EAST_CONNECTION = enumStatePropertyOf(minecraft("east"),
            LanternKeys.REDSTONE_EAST_CONNECTION as K<LanternWireAttachmentType>)

    @JvmField val REDSTONE_WEST_CONNECTION = enumStatePropertyOf(minecraft("west"),
            LanternKeys.REDSTONE_WEST_CONNECTION as K<LanternWireAttachmentType>)

    @JvmField val INSTRUMENT = enumStatePropertyOf(minecraft("instrument"),
            LanternKeys.INSTRUMENT_TYPE as K<LanternInstrumentType>)

    // Int state properties

    @JvmField val SAPLING_GROWTH_STAGE = intStatePropertyOf(minecraft("stage"), Keys.GROWTH_STAGE, 0..1)

    @JvmField val POWER = intStatePropertyOf(minecraft("power"), Keys.POWER, 0..15)

    @JvmField val MOISTURE = intStatePropertyOf(minecraft("moisture"), Keys.MOISTURE, 0..7)

    @JvmField val ROTATION = intStatePropertyOf(minecraft("rotation"), LanternKeys.FINE_ROTATION, 0..15)

    @JvmField val NOTE = intStatePropertyOf(minecraft("note"), Keys.NOTE_PITCH, 0..24,
            stateKeyValueTransformer(
                    { stateValue -> NotePitchRegistry.require(stateValue) },
                    { keyValue -> NotePitchRegistry.getId(keyValue) }))

    @JvmField val DECAY_DISTANCE = intStatePropertyOf(minecraft("distance"), Keys.DECAY_DISTANCE, 1..7)
}
