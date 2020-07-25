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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.item.inventory.ItemStack

/**
 *
 * @property entityId The entity id
 * @property entries The slot index with the new equipped item stack
 */
data class EntityEquipmentPacket(
        val entityId: Int,
        val entries: Int2ObjectMap<ItemStack?>
) : Packet
