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
package org.lanternpowered.server.network.message.codec.object.serializer;

import java.util.Locale;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.object.LocalizedText;
import org.lanternpowered.server.text.gson.JsonTextTranslatableSerializer;
import org.spongepowered.api.text.Text;

import com.google.gson.JsonSyntaxException;

public final class SerializerLocalizedText implements ObjectSerializer<LocalizedText> {

    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, LocalizedText object) throws CodecException {
        final Locale oldLocale = JsonTextTranslatableSerializer.getCurrentLocale();
        JsonTextTranslatableSerializer.setCurrentLocale(object.getLocale());
        context.write(buf, String.class, SerializerText.GSON.toJson(object.getText()));
        JsonTextTranslatableSerializer.setCurrentLocale(oldLocale);
    }

    @Override
    public LocalizedText read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        try {
            return new LocalizedText(SerializerText.GSON.fromJson(context.read(buf, String.class), Text.class),
                    JsonTextTranslatableSerializer.getCurrentLocale());
        } catch (JsonSyntaxException e) {
            throw new CodecException(e);
        }
    }
}
