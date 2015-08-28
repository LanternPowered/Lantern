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
