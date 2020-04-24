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
import org.spongepowered.math.vector.Vector3d
import java.util.UUID

data class SpawnObjectMessage(
        val entityId: Int,
        val uniqueId: UUID,
        val objectType: Int,
        val objectData: Int,
        val position: Vector3d,
        val yaw: Int,
        val pitch: Int,
        val velocity: Vector3d
) : Message
