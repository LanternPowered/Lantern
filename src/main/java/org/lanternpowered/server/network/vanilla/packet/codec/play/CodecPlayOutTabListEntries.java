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
package org.lanternpowered.server.network.vanilla.packet.codec.play;

import io.netty.handler.codec.CodecException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTabListEntries;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutTabListEntries.Entry;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class CodecPlayOutTabListEntries implements Codec<PacketPlayOutTabListEntries> {

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
        this.typeLookup = Object2IntMaps.unmodifiable(typeLookup);
    }

    @Override
    public ByteBuffer encode(CodecContext context, PacketPlayOutTabListEntries message) throws CodecException {
        final ByteBuffer buf = context.byteBufAlloc().buffer();
        final List<Entry> entries = message.getEntries();
        final int type = this.typeLookup.getInt(entries.get(0).getClass());
        buf.writeVarInt(type);
        buf.writeVarInt(entries.size());
        for (Entry entry : entries) {
            buf.writeUniqueId(entry.getGameProfile().getUniqueId());
            switch (type) {
                case ADD:
                    buf.writeString(entry.getGameProfile().getName().orElse("unknown"));
                    final Collection<ProfileProperty> properties = entry.getGameProfile().getPropertyMap().values();
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
                    Text displayName = entry.getDisplayName();
                    boolean flag = displayName != null;
                    buf.writeBoolean(flag);
                    if (flag) {
                        context.write(buf, ContextualValueTypes.TEXT, displayName);
                    }
                    break;
                case UPDATE_GAME_MODE:
                    buf.writeVarInt(((LanternGameMode) entry.getGameMode()).getInternalId());
                    break;
                case UPDATE_LATENCY:
                    buf.writeVarInt(entry.getPing());
                    break;
                case UPDATE_DISPLAY_NAME:
                    displayName = entry.getDisplayName();
                    flag = displayName != null;
                    buf.writeBoolean(flag);
                    if (flag) {
                        context.write(buf, ContextualValueTypes.TEXT, displayName);
                    }
                    break;
                case REMOVE:
                    break;
            }
        }
        return buf;
    }
}
