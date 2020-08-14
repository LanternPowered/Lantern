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
package org.lanternpowered.server.network.item

import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.server.network.buffer.ByteBuffer

object NetworkItemHelper {

    fun readTypeFrom(buf: ByteBuffer): ItemType {
        val networkId = buf.readVarInt()
        return NetworkItemTypeRegistry.getByNetworkId(networkId)?.type
                ?: throw DecoderException("Received ItemStack with unknown network id: $networkId")
    }

    fun writeTypeTo(buf: ByteBuffer, type: ItemType) {
        val networkType = NetworkItemTypeRegistry.getByType(type)
                ?: throw EncoderException("Item type isn't registered: $type")
        buf.writeVarInt(networkType.networkId)
    }

    fun readRawFrom(buf: ByteBuffer): RawItemStack? {
        val exists = buf.readBoolean()
        if (!exists)
            return null
        val networkId = buf.readVarInt()
        if (networkId == -1)
            return null
        val key = NetworkItemTypeRegistry.getVanillaKeyByNetworkId(networkId)
                ?: throw DecoderException("Received ItemStack with unknown network id: $networkId")
        val amount = buf.readByte().toInt()
        val dataView = buf.readDataView()
        return RawItemStack(key, amount, dataView)
    }

    fun writeRawTo(buf: ByteBuffer, itemStack: RawItemStack?) {
        if (itemStack == null || itemStack.amount <= 0) {
            buf.writeBoolean(false)
        } else {
            val networkType = NetworkItemTypeRegistry.getByKey(itemStack.type)
                    ?: throw EncoderException("Invalid item type: ${itemStack.type}")
            buf.writeBoolean(true)
            buf.writeVarInt(networkType.networkId)
            buf.writeByte(itemStack.amount.toByte())
            buf.writeDataView(itemStack.dataView)
        }
    }
}
