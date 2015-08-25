package org.lanternpowered.server.network.vanilla.message.codec.play;

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

    private CodecUtils() {
    }

}
