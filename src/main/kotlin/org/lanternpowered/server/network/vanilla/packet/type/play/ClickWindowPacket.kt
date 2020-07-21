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

data class ClickWindowPacket(
        val windowId: Int,
        val slot: Int,
        val mode: Int,
        val button: Int,
        val transaction: Int,
        val clickedItem: ItemStack?
) : Packet
