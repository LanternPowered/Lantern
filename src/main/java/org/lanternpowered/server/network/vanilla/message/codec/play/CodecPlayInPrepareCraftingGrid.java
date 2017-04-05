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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import com.google.common.collect.ImmutableList;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.objects.Types;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPrepareCraftingGrid;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public final class CodecPlayInPrepareCraftingGrid implements Codec<MessagePlayInPrepareCraftingGrid> {

    @Override
    public MessagePlayInPrepareCraftingGrid decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int windowId = buf.readByte();
        final int transactionId = buf.readShort();
        final List<MessagePlayInPrepareCraftingGrid.SlotUpdate> preparedItems = readUpdates(buf);
        final List<MessagePlayInPrepareCraftingGrid.SlotUpdate> returnedItems = readUpdates(buf);
        return new MessagePlayInPrepareCraftingGrid(windowId, transactionId, preparedItems, returnedItems);
    }

    private static List<MessagePlayInPrepareCraftingGrid.SlotUpdate> readUpdates(ByteBuffer buf) {
        final ImmutableList.Builder<MessagePlayInPrepareCraftingGrid.SlotUpdate> builder = ImmutableList.builder();
        final int count = buf.readByte();
        for (int i = 0; i < count; i++) {
            final ItemStack itemStack = buf.read(Types.ITEM_STACK);
            final int craftingSlot = buf.readByte();
            final int playerSlot = buf.readByte();
            builder.add(new MessagePlayInPrepareCraftingGrid.SlotUpdate(itemStack, craftingSlot, playerSlot));
        }
        return builder.build();
    }
}
