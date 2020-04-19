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
package org.lanternpowered.server.script;

import java.util.Random;

public class LanternRandom extends Random {

    public static final LanternRandom $random = new LanternRandom();

    public float range(float min, float max) {
        return min + this.nextFloat() * (max - min);
    }

    public float range(float max) {
        return this.nextFloat() * max;
    }

    public double range(double min, double max) {
        return min + this.nextDouble() * (max - min);
    }

    public double range(double max) {
        return this.nextDouble() * max;
    }

    public int range(int min, int max) {
        return min + this.nextInt(max - min + 1);
    }

    public int range(int max) {
        return this.nextInt(max + 1);
    }
}
