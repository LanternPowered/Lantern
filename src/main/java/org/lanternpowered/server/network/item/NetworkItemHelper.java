/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
