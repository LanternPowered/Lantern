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
package org.lanternpowered.server.network.item;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.ItemType;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class NetworkItemHelper {

    public static ItemType readTypeFrom(ByteBuffer buf) {
        return NetworkItemTypeRegistry.networkIdToItemType.get(buf.readVarInt());
    }

    public static void writeTypeTo(ByteBuffer buf, ItemType type) {
        buf.writeVarInt(NetworkItemTypeRegistry.itemTypeToInternalAndNetworkId.get(type)[1]);
    }

    @Nullable
    public static RawItemStack readRawFrom(ByteBuffer buf) {
        final boolean exists = buf.readBoolean();
        if (!exists) {
            return null;
        }
        final int networkId = buf.readVarInt();
        if (networkId == -1) {
            return null;
        }
        final String itemType = NetworkItemTypeRegistry.networkIdToNormal.get(networkId);
        if (itemType == null) {
            throw new DecoderException("Received ItemStack with unknown network id: " + networkId);
        }
        final int amount = buf.readByte();
        final DataView dataView = buf.readDataView();
        return new RawItemStack(itemType, amount, dataView);
    }

    public static void writeRawTo(ByteBuffer buf, @Nullable RawItemStack itemStack) {
        if (itemStack == null || itemStack.getAmount() <= 0) {
            buf.writeBoolean(false);
        } else {
            final int networkId = NetworkItemTypeRegistry.normalToNetworkId.getInt(itemStack.getType());
            if (networkId == -1) {
                throw new EncoderException("Invalid vanilla/modded item type id: " + itemStack.getType());
            }
            buf.writeBoolean(true);
            buf.writeVarInt(networkId);
            buf.writeByte((byte) itemStack.getAmount());
            buf.writeDataView(itemStack.getDataView());
        }
    }
}
