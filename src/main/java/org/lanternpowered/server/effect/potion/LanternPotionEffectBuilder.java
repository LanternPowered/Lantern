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
package org.lanternpowered.server.effect.potion;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.DataQueries;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Optional;

public class LanternPotionEffectBuilder extends AbstractDataBuilder<PotionEffect> implements PotionEffect.Builder {

    private PotionEffectType potionType;
    private int duration;
    private int amplifier;
    private boolean isAmbient;
    private boolean showParticles;

    public LanternPotionEffectBuilder() {
        super(PotionEffect.class, 1);
        this.reset();
    }

    @Override
    public PotionEffect.Builder from(PotionEffect holder) {
        this.potionType = checkNotNull(holder).getType();
        this.duration = holder.getDuration();
        this.amplifier = holder.getAmplifier();
        this.isAmbient = holder.isAmbient();
        this.showParticles = holder.getShowParticles();
        return this;
    }

    @Override
    protected Optional<PotionEffect> buildContent(DataView container) throws InvalidDataException {
        checkNotNull(container);
        if (!container.contains(DataQueries.POTION_TYPE) || !container.contains(DataQueries.POTION_DURATION)
                || !container.contains(DataQueries.POTION_AMPLIFIER) || !container.contains(DataQueries.POTION_AMBIANCE)
                || !container.contains(DataQueries.POTION_SHOWS_PARTICLES)) {
            return Optional.empty();
        }
        String effectName = container.getString(DataQueries.POTION_TYPE).get();
        Optional<PotionEffectType> optional = Sponge.getRegistry().getType(PotionEffectType.class, effectName);
        if (!optional.isPresent()) {
            throw new InvalidDataException("The container has an invalid potion type name: " + effectName);
        }
        int duration = container.getInt(DataQueries.POTION_DURATION).get();
        int amplifier = container.getInt(DataQueries.POTION_AMPLIFIER).get();
        boolean ambient = container.getBoolean(DataQueries.POTION_AMBIANCE).get();
        boolean showParticles = container.getBoolean(DataQueries.POTION_SHOWS_PARTICLES).get();
        return Optional.of(new LanternPotionEffect(optional.get(), duration, amplifier, ambient, showParticles));
    }

    @Override
    public PotionEffect.Builder potionType(PotionEffectType potionEffectType) {
        checkNotNull(potionEffectType, "Potion effect type cannot be null");
        this.potionType = potionEffectType;
        return this;
    }

    @Override
    public PotionEffect.Builder duration(int duration) {
        checkArgument(duration > 0, "Duration must be greater than 0");
        this.duration = duration;
        return this;
    }

    @Override
    public PotionEffect.Builder amplifier(int amplifier) throws IllegalArgumentException {
        this.amplifier = amplifier;
        return this;
    }

    // Sponge, here is a typo
    @Override
    public PotionEffect.Builder ambience(boolean ambience) {
        this.isAmbient = ambience;
        return this;
    }

    @Override
    public PotionEffect.Builder particles(boolean showsParticles) {
        this.showParticles = showsParticles;
        return this;
    }

    @Override
    public PotionEffect build() throws IllegalStateException {
        checkState(this.potionType != null, "Potion type has not been set");
        checkState(this.duration > 0, "Duration has not been set");
        return new LanternPotionEffect(this.potionType, this.duration, this.amplifier, this.isAmbient, this.showParticles);
    }

    @Override
    public PotionEffect.Builder reset() {
        this.potionType = null;
        this.amplifier = 0;
        this.duration = 0;
        this.isAmbient = true;
        this.showParticles = true;
        return this;
    }
}
