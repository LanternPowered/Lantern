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
package org.lanternpowered.server.block;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.block.BlockSoundGroup;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.util.ResettableBuilder;

public final class LanternBlockSoundGroup implements BlockSoundGroup {

    /**
     * Constructs a new {@link Builder}.
     *
     * @return The builder
     */
    public static Builder builder() {
        return new Builder();
    }

    private final double volume;
    private final double pitch;

    private final SoundType breakSound;
    private final SoundType stepSound;
    private final SoundType placeSound;
    private final SoundType hitSound;
    private final SoundType fallSound;

    private LanternBlockSoundGroup(Builder builder) {
        this.volume = builder.volume;
        this.pitch = builder.pitch;
        this.breakSound = builder.breakSound;
        this.stepSound = builder.stepSound;
        this.placeSound = builder.placeSound;
        this.hitSound = builder.hitSound;
        this.fallSound = builder.fallSound;
    }

    @Override
    public double getVolume() {
        return this.volume;
    }

    @Override
    public double getPitch() {
        return this.pitch;
    }

    @Override
    public SoundType getBreakSound() {
        return this.breakSound;
    }

    @Override
    public SoundType getStepSound() {
        return this.stepSound;
    }

    @Override
    public SoundType getPlaceSound() {
        return this.placeSound;
    }

    @Override
    public SoundType getHitSound() {
        return this.hitSound;
    }

    @Override
    public SoundType getFallSound() {
        return this.fallSound;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("volume", this.volume)
                .add("pitch", this.pitch)
                .add("breakSound", this.breakSound)
                .add("stepSound", this.stepSound)
                .add("placeSound", this.placeSound)
                .add("hitSound", this.hitSound)
                .add("fallSound", this.fallSound)
                .toString();
    }

    public static final class Builder implements ResettableBuilder<LanternBlockSoundGroup, Builder> {

        private SoundType breakSound;
        private SoundType stepSound;
        private SoundType placeSound;
        private SoundType hitSound;
        private SoundType fallSound;

        private double volume;
        private double pitch;

        Builder() {
            reset();
        }

        /**
         * Sets the volume.
         *
         * @param volume The volume
         * @return This builder, for chaining
         */
        public Builder volume(double volume) {
            this.volume = volume;
            return this;
        }

        /**
         * Sets the pitch.
         *
         * @param pitch The pitch
         * @return This builder, for chaining
         */
        public Builder pitch(double pitch) {
            this.pitch = pitch;
            return this;
        }

        /**
         * Sets the break {@link SoundType}.
         *
         * @param breakSound The break sound
         * @return This builder, for chaining
         */
        public Builder breakSound(SoundType breakSound) {
            this.breakSound = breakSound;
            return this;
        }

        /**
         * Sets the step {@link SoundType}.
         *
         * @param stepSound The step sound
         * @return This builder, for chaining
         */
        public Builder stepSound(SoundType stepSound) {
            this.stepSound = stepSound;
            return this;
        }

        /**
         * Sets the place {@link SoundType}.
         *
         * @param placeSound The place sound
         * @return This builder, for chaining
         */
        public Builder placeSound(SoundType placeSound) {
            this.placeSound = placeSound;
            return this;
        }

        /**
         * Sets the hit {@link SoundType}.
         *
         * @param hitSound The hit sound
         * @return This builder, for chaining
         */
        public Builder hitSound(SoundType hitSound) {
            this.hitSound = hitSound;
            return this;
        }

        /**
         * Sets the fall {@link SoundType}.
         *
         * @param fallSound The fall sound
         * @return This builder, for chaining
         */
        public Builder fallSound(SoundType fallSound) {
            this.fallSound = fallSound;
            return this;
        }

        /**
         * Builds the {@link LanternBlockSoundGroup}.
         *
         * @return The block sound group
         */
        public LanternBlockSoundGroup build() {
            checkNotNull(this.breakSound, "breakSound must be set");
            checkNotNull(this.fallSound, "fallSound must be set");
            checkNotNull(this.hitSound, "hitSound must be set");
            checkNotNull(this.placeSound, "placeSound must be set");
            checkNotNull(this.stepSound, "stepSound must be set");
            return new LanternBlockSoundGroup(this);
        }

        @Override
        public Builder from(LanternBlockSoundGroup value) {
            this.breakSound = value.breakSound;
            this.fallSound = value.fallSound;
            this.hitSound = value.hitSound;
            this.placeSound = value.placeSound;
            this.stepSound = value.stepSound;
            this.volume = value.volume;
            this.pitch = value.pitch;
            return this;
        }

        @Override
        public Builder reset() {
            this.breakSound = null;
            this.fallSound = null;
            this.hitSound = null;
            this.placeSound = null;
            this.stepSound = null;
            this.volume = 1.0;
            this.pitch = 1.0;
            return this;
        }
    }
}
