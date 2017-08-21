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

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.objects.Types;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInCreativeWindowAction;
import org.spongepowered.api.item.inventory.ItemStack;

public final class CodecPlayInCreativeWindowAction implements Codec<MessagePlayInCreativeWindowAction> {

    @Override
    public MessagePlayInCreativeWindowAction decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final int slot = buf.readShort();
        final ItemStack item = buf.read(Types.ITEM_STACK);
        // Consume the trailing bytes, there is currently a bug
        // in Forge that still allows access to unavailable item
        // types if they are searched for in the creative search.
        // These unavailable items are hidden in all the other tabs,
        // these also have a 'none' type id -1 which makes it not
        // possible to differentiate from the 'none' item in the
        // type serializer
        // This prevents spam of trailing bytes in this message.
        if (item == null) { // Is null if id == -1
            buf.setReadIndex(buf.readerIndex() + buf.available());
        }
        return new MessagePlayInCreativeWindowAction(slot, item);
    }
}
