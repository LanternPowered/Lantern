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
package org.lanternpowered.server.network.vanilla.message.type.play

import org.lanternpowered.server.network.message.Message
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.util.Direction
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3i

data class ClientBlockPlacementMessage(
        val position: Vector3i,
        val clickOffset: Vector3d,
        val face: Direction,
        val handType: HandType,
        val insideBlock: Boolean
) : Message
