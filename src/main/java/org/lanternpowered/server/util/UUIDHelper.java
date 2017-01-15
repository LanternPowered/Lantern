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
package org.lanternpowered.server.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

public final class UUIDHelper {

    private UUIDHelper() {
    }

    /**
     * Parses the {@link UUID} instance from a flat string (without dashes).
     * 
     * @param string The flat string
     * @return The unique id
     */
    public static UUID fromFlatString(String string) {
        checkNotNull(string, "string");
        checkArgument(string.length() == 32, "length must be 32");
        return UUID.fromString(string.substring(0, 8) + "-" + string.substring(8, 12) + "-" + string.substring(12, 16) +
                "-" + string.substring(16, 20) + "-" + string.substring(20, 32));
    }

    /**
     * Converts the {@link UUID} to a flat string (without dashes).
     * 
     * @param uniqueId The unique id
     * @return The flat string
     */
    public static String toFlatString(UUID uniqueId) {
        return checkNotNull(uniqueId, "uniqueId").toString().replace("-", "");
    }

    public static UUID modifyVersion(UUID uniqueId, int version) {
        checkNotNull(uniqueId, "uniqueId");
        checkArgument(version >= 0 && version <= 15, "Invalid version number");
        long most = uniqueId.getMostSignificantBits();
        most &= 0xFFFFFFFFFFFF0FFFL; // Clear the version
        most |= version << 12; // Set the version
        return new UUID(most, uniqueId.getLeastSignificantBits());
    }
}
