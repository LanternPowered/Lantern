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
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.lanternpowered.server.advancement.LanternAdvancementType;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.objects.Types;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAdvancements;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CodecPlayOutAdvancements implements Codec<MessagePlayOutAdvancements> {

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutAdvancements message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeBoolean(message.getClear());
        final List<MessagePlayOutAdvancements.AdvStruct> addedAdvStructs = message.getAddedAdvStructs();
        buf.writeVarInt(addedAdvStructs.size());
        for (MessagePlayOutAdvancements.AdvStruct struct : addedAdvStructs) {
            buf.writeString(struct.getId());
            final Optional<String> optParent = struct.getParentId();
            buf.writeBoolean(optParent.isPresent());
            optParent.ifPresent(buf::writeString);
            final Optional<MessagePlayOutAdvancements.AdvStruct.Display> optDisplay = struct.getDisplay();
            buf.writeBoolean(optDisplay.isPresent());
            if (optDisplay.isPresent()) {
                final MessagePlayOutAdvancements.AdvStruct.Display display = optDisplay.get();
                buf.write(Types.LOCALIZED_TEXT, display.getTitle());
                buf.write(Types.LOCALIZED_TEXT, display.getDescription());
                buf.write(Types.ITEM_STACK, display.getIcon().createStack());
                buf.writeVarInt(((LanternAdvancementType) display.getType()).getInternalId());
                final Optional<String> optBackground = display.getBackground();
                int flags = 0;
                if (optBackground.isPresent()) {
                    flags |= 0x1;
                }
                if (display.doesShowToast()) {
                    flags |= 0x2;
                }
                if (display.isHidden()) {
                    flags |= 0x4;
                }
                buf.writeInteger(flags);
                optBackground.ifPresent(buf::writeString);
                buf.writeFloat((float) display.getX());
                buf.writeFloat((float) display.getY());
            }
            final Collection<String> criteria = struct.getCriteria();
            buf.writeVarInt(criteria.size());
            criteria.forEach(buf::writeString);
            final String[][] requirements = struct.getRequirements();
            buf.writeVarInt(requirements.length);
            for (String[] requirements1 : requirements) {
                buf.writeVarInt(requirements1.length);
                for (String requirement : requirements1) {
                    buf.writeString(requirement);
                }
            }
        }
        final List<String> removed = message.getRemovedAdvs();
        buf.writeVarInt(removed.size());
        removed.forEach(buf::writeString);
        final Map<String, Object2LongMap<String>> progress = message.getProgress();
        buf.writeVarInt(progress.size());
        for (Map.Entry<String, Object2LongMap<String>> entry : progress.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            for (Object2LongMap.Entry<String> entry1 : entry.getValue().object2LongEntrySet()) {
                buf.writeString(entry1.getKey());
                final long time = entry1.getLongValue();
                buf.writeBoolean(time != -1L);
                if (time != -1L) {
                    buf.writeLong(time);
                }
            }
        }
        return buf;
    }
}
