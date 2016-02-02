/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

import gnu.trove.TCollections;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.serializer.Types;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListEntries.Entry;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class CodecPlayOutTabListEntries implements Codec<MessagePlayOutTabListEntries> {

    private static final int ADD = 0;
    private static final int UPDATE_GAME_MODE = 1;
    private static final int UPDATE_LATENCY = 2;
    private static final int UPDATE_DISPLAY_NAME = 3;
    private static final int REMOVE = 4;

    private final TObjectIntMap<Class<?>> typeLookup;

    {
        TObjectIntMap<Class<?>> typeLookup = new TObjectIntHashMap<>();
        typeLookup.put(Entry.Add.class, ADD);
        typeLookup.put(Entry.UpdateGameMode.class, UPDATE_GAME_MODE);
        typeLookup.put(Entry.UpdateLatency.class, UPDATE_LATENCY);
        typeLookup.put(Entry.UpdateDisplayName.class, UPDATE_DISPLAY_NAME);
        typeLookup.put(Entry.Remove.class, REMOVE);
        this.typeLookup = TCollections.unmodifiableMap(typeLookup);
    }

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutTabListEntries message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        List<Entry> entries = message.getEntries();
        int type = this.typeLookup.get(entries.get(0).getClass());
        context.writeVarInt(buf, type);
        context.writeVarInt(buf, entries.size());
        for (Entry entry : entries) {
            switch (type) {
                case ADD:
                    context.write(buf, Types.STRING, entry.getName());
                    Collection<ProfileProperty> properties = entry.getProperties();
                    context.writeVarInt(buf, properties.size());
                    for (ProfileProperty property : properties) {
                        context.write(buf, Types.STRING, property.getName());
                        context.write(buf, Types.STRING, property.getValue());
                        Optional<String> signature = property.getSignature();
                        boolean flag = signature.isPresent();
                        buf.writeBoolean(flag);
                        if (flag) {
                            context.write(buf, Types.STRING, signature.get());
                        }
                    }
                    context.writeVarInt(buf, ((LanternGameMode) entry.getGameMode()).getInternalId());
                    context.writeVarInt(buf, entry.getPing());
                    Text displayName = entry.getDisplayName();
                    boolean flag = displayName != null;
                    buf.writeBoolean(flag);
                    if (flag) {
                        context.write(buf, Types.TEXT, displayName);
                    }
                    break;
                case UPDATE_GAME_MODE:
                    context.writeVarInt(buf, ((LanternGameMode) entry.getGameMode()).getInternalId());
                    break;
                case UPDATE_LATENCY:
                    context.writeVarInt(buf, entry.getPing());
                    break;
                case UPDATE_DISPLAY_NAME:
                    Text displayName0 = entry.getDisplayName();
                    boolean flag0 = displayName0 != null;
                    buf.writeBoolean(flag0);
                    if (flag0) {
                        context.write(buf, Types.TEXT, displayName0);
                    }
                    break;
                case REMOVE:
                    break;
            }
        }
        return buf;
    }
}
