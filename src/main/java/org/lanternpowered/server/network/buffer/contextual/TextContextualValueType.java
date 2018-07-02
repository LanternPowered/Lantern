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
package org.lanternpowered.server.network.buffer.contextual;

import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.text.gson.JsonTextSerializer;
import org.lanternpowered.server.text.translation.TranslationContext;
import org.spongepowered.api.text.Text;

final class TextContextualValueType implements ContextualValueType<Text> {

    @Override
    public void write(CodecContext ctx, Text object, ByteBuffer buf) throws CodecException {
        try (TranslationContext ignored = TranslationContext.enter()
                .locale(ctx.getSession().getLocale())
                .enableForcedTranslations()) {
            buf.writeString(fixJson(JsonTextSerializer.getGson().toJson(object)));
        }
    }

    @Override
    public Text read(CodecContext ctx, ByteBuffer buf) throws CodecException {
        try (TranslationContext ignored = TranslationContext.enter()
                .locale(ctx.getSession().getLocale())
                .enableForcedTranslations()) {
            return JsonTextSerializer.getGson().fromJson(buf.readString(), Text.class);
        }
    }

    // We need to fix the json format yay, the minecraft client
    // can't handle primitives or arrays as root, just expect
    // things to break, so fix it...
    private static String fixJson(String json) {
        final char start = json.charAt(0);
        if (start == '{') {
            return json;
        } else {
            if (start != '[') {
                json = '[' + json + ']';
            }
            return "{\"text\":\"\",\"extra\":" + json + "}";
        }
    }
}
