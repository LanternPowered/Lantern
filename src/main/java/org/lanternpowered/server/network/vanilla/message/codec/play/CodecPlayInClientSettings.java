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
import org.lanternpowered.server.game.registry.type.text.ChatVisibilityRegistryModule;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInClientSettings;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.HandPreferences;
import org.spongepowered.api.text.chat.ChatVisibility;

import java.util.Locale;

public final class CodecPlayInClientSettings implements Codec<MessagePlayInClientSettings> {

    @Override
    public MessagePlayInClientSettings decode(CodecContext context, ByteBuffer buf) throws CodecException {
        // The locale is lowercase, this is not allowed
        final String localeName = buf.readLimitedString(16);
        final String[] parts = localeName.split("_", 3);
        Locale locale;
        if (parts.length == 3) {
            locale = new Locale(parts[0].toLowerCase(), parts[1].toUpperCase(), parts[2]);
        } else if (parts.length == 2) {
            locale = new Locale(parts[0].toLowerCase(), parts[1].toUpperCase());
        } else {
            locale = new Locale(parts[0]);
        }
        final int viewDistance = buf.readByte();
        final ChatVisibility visibility = ChatVisibilityRegistryModule.get().getByInternalId(buf.readByte()).get();
        final boolean enableColors = buf.readBoolean();
        final int skinPartsBitPattern = buf.readByte() & 0xff;
        final HandPreference dominantHand = buf.readVarInt() == 1 ? HandPreferences.RIGHT : HandPreferences.LEFT;
        return new MessagePlayInClientSettings(locale, viewDistance, visibility, dominantHand, enableColors, skinPartsBitPattern);
    }
}
