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
package org.lanternpowered.server.network.vanilla.packet.codec.play

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntMaps
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.spongepowered.api.util.Direction
import org.spongepowered.math.GenericMath

object CodecUtils {

    private val directions = arrayOf(
            Direction.DOWN,
            Direction.UP,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST
    )

    private var directionToValue: Object2IntMap<Direction>

    /**
     * Decodes the integer into a [Direction].
     *
     * @param index The direction value
     * @return The direction
     */
    fun decodeDirection(index: Int): Direction {
        check(index >= 0 && index < this.directions.size) { "Unknown direction value: $index" }
        return this.directions[index]
    }

    /**
     * Decodes the [Direction] value into a integer.
     *
     * @param direction The direction
     * @return The integer direction
     */
    fun encodeDirection(direction: Direction): Int {
        val value = this.directionToValue.getInt(direction)
        check(value != -1) { "Unsupported direction: $direction" }
        return value
    }

    init {
        val map = Object2IntOpenHashMap<Direction>()
        map.defaultReturnValue(-1)
        for (i in this.directions.indices)
            map[this.directions[i]] = i
        this.directionToValue = Object2IntMaps.unmodifiable(map)
    }
}
