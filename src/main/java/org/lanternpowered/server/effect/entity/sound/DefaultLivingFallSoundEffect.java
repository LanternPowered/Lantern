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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.block.BlockProperties;
import org.lanternpowered.server.effect.entity.AbstractEntityEffect;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.event.LanternEventContextKeys;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.util.Random;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultLivingFallSoundEffect extends AbstractEntityEffect {

    private final SoundType fallSoundType;
    @Nullable private final SoundType bigFallSoundType;

    public DefaultLivingFallSoundEffect(SoundType fallSoundType, @Nullable SoundType bigFallSoundType) {
        checkNotNull(fallSoundType, "fallSoundType");
        this.fallSoundType = fallSoundType;
        this.bigFallSoundType = bigFallSoundType;
    }

    public DefaultLivingFallSoundEffect(SoundType fallSoundType) {
        this(fallSoundType, null);
    }

    @Override
    protected void play(LanternEntity entity, Vector3d relativePosition, Random random) {
        SoundType soundType = this.fallSoundType;
        // A big fall sound, if the distance (damage) was high enough
        if (this.bigFallSoundType != null) {
            final double baseDamage = CauseStack.current().getContext(LanternEventContextKeys.BASE_DAMAGE_VALUE).orElse(0.0);
            if (baseDamage > 4.0) {
                soundType = this.bigFallSoundType;
            }
        }
        entity.playSound(soundType, 1.0, 1.0);

        // Play a sound for hitting the ground
        final Vector3i blockPos = entity.getPosition().add(0, -0.2, 0).toInt();
        entity.getWorld().getProperty(blockPos, BlockProperties.BLOCK_SOUND_GROUP).ifPresent(
                soundGroup -> entity.playSound(soundGroup.getFallSound(), soundGroup.getVolume() * 0.5, soundGroup.getPitch() * 0.75));
    }
}
