/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import java.util.Locale;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.caching.CachingHashGenerator;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.object.serializer.ObjectSerializerContext;
import org.lanternpowered.server.network.vanilla.message.processor.play.ProcessorPlayInClientSettings;

public class CodecUtils {

    /**
     * Wraps the float angle into a byte.
     * 
     * @param angle the angle
     * @return the byte
     */
    public static byte wrapAngle(float angle) {
        while (angle >= 360f) {
            angle -= 360f;
        }
        while (angle < 0) {
            angle += 360f;
        }
        return (byte) ((angle / 360f) * 256f);
    }

    /**
     * Unwraps the byte angle into a float.
     * 
     * @param angle the angle
     * @return the float
     */
    public static float unwrapAngle(byte angle) {
        return ((float) angle / 256f) * 360f;
    }

    public static Locale getLocale(ObjectSerializerContext context) {
        if (context instanceof CodecContext) {
            Locale locale0 = ((CodecContext) context).channel().attr(ProcessorPlayInClientSettings.LOCALE).get();
            if (locale0 != null) {
                return locale0;
            }
        }
        return Locale.ENGLISH;
    }

    public static class LocaleCachingHash implements CachingHashGenerator<Message> {

        @Override
        public int generate(CodecContext context, Message message) {
            return getLocale(context).hashCode();
        }
    }

    private CodecUtils() {
    }

}
