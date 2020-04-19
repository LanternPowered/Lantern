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
package org.lanternpowered.server.data.persistence.nbt;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

enum NbtType {
    // Official types
    END                     (0),
    BYTE                    (1),
    SHORT                   (2),
    INT                     (3),
    LONG                    (4),
    FLOAT                   (5),
    DOUBLE                  (6),
    BYTE_ARRAY              (7),
    STRING                  (8),
    LIST                    (9),
    COMPOUND                (10),
    INT_ARRAY               (11),
    LONG_ARRAY              (12),

    // Sponge and lantern types, but remaining
    // compatible with the official ones.
    BOOLEAN                 (1, "Boolean"), // Boolean was used before, so still uppercase
    BOOLEAN_ARRAY           (7, "boolean[]"),
    SHORT_ARRAY             (9, "short[]"),
    FLOAT_ARRAY             (9, "float[]"),
    DOUBLE_ARRAY            (9, "double[]"),
    STRING_ARRAY            (9, "string[]"),
    CHAR                    (8, "char"),
    CHAR_ARRAY              (8, "char[]"),
    COMPOUND_ARRAY          (9, "compound[]"),
    MAP                     (9, "map"),
    MAP_ARRAY               (9, "map[]"),

    UNKNOWN                 (99),
    ;

    static final String mapKeyName = "K";
    static final String mapValueName = "V";

    static final Map<String, NbtType> bySuffix = new HashMap<>();
    static final Int2ObjectMap<NbtType> byIndex = new Int2ObjectOpenHashMap<>();

    final int type;
    @Nullable final String suffix;

    NbtType(int type) {
        this(type, null);
    }

    NbtType(int type, @Nullable String suffix) {
        this.suffix = suffix;
        this.type = type;
    }

    static {
        for (NbtType nbtType : values()) {
            bySuffix.put(nbtType.suffix, nbtType);
            if (nbtType.suffix == null) {
                byIndex.put(nbtType.type, nbtType);
            }
        }
    }
}
