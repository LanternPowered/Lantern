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
package org.lanternpowered.server.effect.entity;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.entity.EntityBodyPosition;
import org.lanternpowered.server.entity.LanternEntity;
import org.spongepowered.api.data.property.entity.EyeHeightProperty;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("ConstantConditions")
public abstract class AbstractEntityEffect implements EntityEffect {

    private final EntityBodyPosition position;

    protected AbstractEntityEffect(EntityBodyPosition position) {
        this.position = position;
    }

    protected AbstractEntityEffect() {
        this(EntityBodyPosition.FEET);
    }

    @Override
    public void play(LanternEntity entity) {
        final Random random = ThreadLocalRandom.current();
        Vector3d relativePosition = Vector3d.ZERO;
        if (this.position == EntityBodyPosition.HEAD) {
            final EyeHeightProperty eyeHeightProperty = entity.getProperty(EyeHeightProperty.class).orElse(null);
            if (eyeHeightProperty != null) {
                relativePosition = new Vector3d(0, eyeHeightProperty.getValue(), 0);
            }
        }
        play(entity, relativePosition, random);
    }

    protected abstract void play(LanternEntity entity, Vector3d relativePosition, Random random);
}
