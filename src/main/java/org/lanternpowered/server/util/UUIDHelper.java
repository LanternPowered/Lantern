package org.lanternpowered.server.util;

import java.util.UUID;

public final class UUIDHelper {

    /**
     * Parses the uuid instance from a flat string (without dashes).
     * 
     * @param string the flat string
     * @return the uuid
     */
    public static UUID fromFlatString(String string) {
        return UUID.fromString(string.substring(0, 8) + "-" + string.substring(8, 12) + "-" + string.substring(12, 16) +
                "-" + string.substring(16, 20) + "-" + string.substring(20, 32));
    }

    /**
     * Converts the uuid to a flat string (without dashes).
     * 
     * @param uuid the uuid
     * @return the flat string
     */
    public static String toFlatString(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
