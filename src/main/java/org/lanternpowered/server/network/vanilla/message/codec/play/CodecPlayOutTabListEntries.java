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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.buffer.objects.Types;
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

    private final Object2IntMap<Class<?>> typeLookup;

    {
        final Object2IntMap<Class<?>> typeLookup = new Object2IntOpenHashMap<>();
        typeLookup.put(Entry.Add.class, ADD);
        typeLookup.put(Entry.UpdateGameMode.class, UPDATE_GAME_MODE);
        typeLookup.put(Entry.UpdateLatency.class, UPDATE_LATENCY);
        typeLookup.put(Entry.UpdateDisplayName.class, UPDATE_DISPLAY_NAME);
        typeLookup.put(Entry.Remove.class, REMOVE);
        typeLookup.defaultReturnValue(-1);
        this.typeLookup = Object2IntMaps.unmodifiable(typeLookup);
    }

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutTabListEntries message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        final List<Entry> entries = message.getEntries();
        final int type = this.typeLookup.getInt(entries.get(0).getClass());
        if (type == -1) {
            throw new CodecException("Illegal entry type: " + entries.get(0).getClass().getName());
        }
        buf.writeVarInt(type);
        buf.writeVarInt(entries.size());
        for (Entry entry : entries) {
            buf.writeUniqueId(entry.getUniqueId());
            switch (type) {
                case ADD:
                    buf.writeString(entry.getName());
                    final Collection<ProfileProperty> properties = entry.getProperties();
                    buf.writeVarInt(properties.size());
                    for (ProfileProperty property : properties) {
                        buf.writeString(property.getName());
                        buf.writeString(property.getValue());
                        final Optional<String> signature = property.getSignature();
                        final boolean flag = signature.isPresent();
                        buf.writeBoolean(flag);
                        if (flag) {
                            buf.writeString(signature.get());
                        }
                    }
                    buf.writeVarInt(((LanternGameMode) entry.getGameMode()).getInternalId());
                    buf.writeVarInt(entry.getPing());
                    final Text displayName = entry.getDisplayName();
                    final boolean flag = displayName != null;
                    buf.writeBoolean(flag);
                    if (flag) {
                        buf.write(Types.TEXT, displayName);
                    }
                    break;
                case UPDATE_GAME_MODE:
                    buf.writeVarInt(((LanternGameMode) entry.getGameMode()).getInternalId());
                    break;
                case UPDATE_LATENCY:
                    buf.writeVarInt(entry.getPing());
                    break;
                case UPDATE_DISPLAY_NAME:
                    final Text displayName0 = entry.getDisplayName();
                    final boolean flag0 = displayName0 != null;
                    buf.writeBoolean(flag0);
                    if (flag0) {
                        buf.write(Types.TEXT, displayName0);
                    }
                    break;
                case REMOVE:
                    break;
            }
        }
        return buf;
    }
}
