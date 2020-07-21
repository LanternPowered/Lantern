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

import org.lanternpowered.server.network.message.Packet
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.math.vector.Vector3d
import org.spongepowered.math.vector.Vector3f

data class SpawnParticlePacket @JvmOverloads constructor(
        val particleId: Int,
        val position: Vector3d,
        val offset: Vector3f,
        val data: Float,
        val quantity: Int,
        val extra: Data?,
        val isLongDistance: Boolean = true
) : Packet {

    abstract class Data

    data class ItemData(val itemStack: ItemStack) : Data()
    data class BlockData(val blockState: Int) : Data()
    data class DustData(val red: Float, val green: Float, val blue: Float, val scale: Float) : Data()
}
