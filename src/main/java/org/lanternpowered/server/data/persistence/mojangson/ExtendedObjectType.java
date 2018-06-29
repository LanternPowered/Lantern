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
package org.lanternpowered.server.data.persistence.mojangson;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

enum ExtendedObjectType {
    BOOLEAN         ("Boolean"), // Boolean is for parsing only, to be compatible with the NBT format
    BOOLEAN_ARRAY   ("boolean[]"),
    SHORT_ARRAY     ("short[]"),
    FLOAT_ARRAY     ("float[]"),
    DOUBLE_ARRAY    ("double[]"),
    STRING_ARRAY    ("string[]"),
    CHAR            ("char"),
    CHAR_ARRAY      ("char[]"),
    VIEW_ARRAY      ("compound[]"),
    MAP             ("map"),
    MAP_ARRAY       ("map[]"),
    ;

    static final String mapKeyName = "K";
    static final String mapValueName = "V";

    static final Map<String, ExtendedObjectType> bySuffix = new HashMap<>();

    @Nullable final String suffix;

    ExtendedObjectType(@Nullable String suffix) {
        this.suffix = suffix;
    }

    static {
        for (ExtendedObjectType nbtType : values()) {
            bySuffix.put(nbtType.suffix, nbtType);
        }
    }
}
