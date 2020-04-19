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
package org.lanternpowered.server.util;

public class BitHelper {

    public static int requiredBits(int value) {
        for (int i = Integer.SIZE - 1; i >= 0; i--) {
            if ((value >> i) != 0) {
                return i + 1;
            }
        }
        return 1; // 0 always needs one bit
    }
}
