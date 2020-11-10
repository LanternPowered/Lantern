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
package org.lanternpowered.server.network.vanilla.packet.type.play

import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.math.vector.Vector3i

data class ClientUpdateStructureBlockPacket(
        val position: Vector3i,
        val specialAction: SpecialAction,
        val mode: Mode,
        val name: String,
        val offset: Vector3i,
        val size: Vector3i,
        val mirror: Mirror,
        val rotation: Rotation,
        val metadata: String,
        val integrity: Double,
        val seed: Long,
        val ignoreEntities: Boolean,
        val showAir: Boolean,
        val showBoundingBox: Boolean
) : Packet {

    enum class Mirror {
        NONE,
        LEFT_RIGHT,
        FRONT_BACK
    }

    enum class Mode {
        SAVE,
        LOAD,
        CORNER,
        DATA
    }

    enum class SpecialAction {
        NONE,
        SAVE_STRUCTURE,
        LOAD_STRUCTURE,
        DETECT_SIZE
    }

    enum class Rotation {
        NONE,
        CLOCKWISE_90,
        CLOCKWISE_180,
        COUNTERCLOCKWISE_90
    }
}
