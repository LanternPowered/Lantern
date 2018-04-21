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
package org.lanternpowered.server.effect.entity.sound;

import org.lanternpowered.server.effect.entity.AbstractEntityEffect;
import org.lanternpowered.server.entity.EntityBodyPosition;
import org.lanternpowered.server.entity.LanternEntity;
import org.spongepowered.api.data.key.Keys;

import java.util.Random;

public abstract class AbstractLivingSoundEffect extends AbstractEntityEffect {

    public AbstractLivingSoundEffect(EntityBodyPosition position) {
        super(position);
    }

    /**
     * Gets a randomized volume value for the sound effect.
     *
     * @param random The random
     * @return The volume value
     */
    protected double getVolume(LanternEntity entity, Random random) {
        return 1.0;
    }

    /**
     * Gets a randomized pitch value for the sound effect.
     *
     * @param random The random
     * @return The pitch value
     */
    protected double getPitch(LanternEntity entity, Random random) {
        double value = random.nextFloat() - random.nextFloat() * 0.2;
        // Adults and children use a different pitch value
        if (entity.get(Keys.IS_ADULT).orElse(true)) {
            value += 1.0;
        } else {
            value += 1.5;
        }
        return value;
    }
}
