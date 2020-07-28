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
package org.lanternpowered.server.network.block

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.data.persistence.DataView

/**
 * Represents the blockEntity entity data to send for a specific chunk section.
 */
interface BlockEntityChunkProtocolData {

    val initData: Short2ObjectMap<DataView>

    val packets: List<Packet>
}
