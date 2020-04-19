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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.game.registry.type.text.ChatVisibilityRegistryModule;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;
import org.lanternpowered.server.util.LocaleCache;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.HandPreferences;
import org.spongepowered.api.text.chat.ChatVisibility;

import java.util.Locale;

public final class CodecPlayInClientSettings implements Codec<MessagePlayInClientSettings> {

    @Override
    public MessagePlayInClientSettings decode(CodecContext context, ByteBuffer buf) throws CodecException {
        final Locale locale = LocaleCache.get(buf.readLimitedString(16));
        final int viewDistance = buf.readByte();
        final ChatVisibility visibility = ChatVisibilityRegistryModule.get().getByInternalId(buf.readByte()).get();
        final boolean enableColors = buf.readBoolean();
        final int skinPartsBitPattern = buf.readByte() & 0xff;
        final HandPreference dominantHand = buf.readVarInt() == 1 ? HandPreferences.RIGHT : HandPreferences.LEFT;
        return new MessagePlayInClientSettings(locale, viewDistance, visibility, dominantHand, enableColors, skinPartsBitPattern);
    }
}
