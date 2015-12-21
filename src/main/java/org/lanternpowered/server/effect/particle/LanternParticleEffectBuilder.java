/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.effect.particle;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.BlockParticle;
import org.spongepowered.api.effect.particle.ColoredParticle;
import org.spongepowered.api.effect.particle.ItemParticle;
import org.spongepowered.api.effect.particle.NoteParticle;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ResizableParticle;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Color;

public class LanternParticleEffectBuilder extends AbstractParticleEffectBuilder<ParticleEffect, ParticleEffect.Builder> {

    @Override
    public LanternParticleEffect build() {
        return new LanternParticleEffect(this.type, this.motion, this.offset, this.count);
    }

    public static class Colorable extends AbstractParticleEffectBuilder<ColoredParticle, ColoredParticle.Builder> implements ColoredParticle.Builder {

        private Color color;

        @Override
        public LanternParticleEffectBuilder.Colorable color(Color color) {
            this.color = checkNotNull(color, "color");
            return this;
        }

        @Override
        public LanternParticleEffect.Colorable build() {
            return new LanternParticleEffect.Colorable(this.type, this.motion, this.offset, this.count,
                    this.color == null ? ((ParticleType.Colorable) this.type).getDefaultColor() : this.color);
        }
    }

    public static class Resizable extends AbstractParticleEffectBuilder<ResizableParticle, ResizableParticle.Builder> implements ResizableParticle.Builder {

        private Float size;

        @Override
        public LanternParticleEffectBuilder.Resizable type(ParticleType particleType) {
            checkArgument(particleType instanceof ParticleType.Resizable);
            super.type(particleType);
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Resizable reset() {
            this.size = null;
            super.reset();
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Resizable size(float size) {
            this.size = size;
            return this;
        }

        @Override
        public LanternParticleEffect.Resizable build() {
            return new LanternParticleEffect.Resizable(this.type, this.motion, this.offset, this.count,
                    this.size == null ? ((ParticleType.Resizable) this.type).getDefaultSize() : this.size);
        }
    }

    public static class Note extends AbstractParticleEffectBuilder<NoteParticle, NoteParticle.Builder> implements NoteParticle.Builder {

        private NotePitch note;

        @Override
        public LanternParticleEffectBuilder.Note note(NotePitch note) {
            this.note = checkNotNull(note, "note");
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Note type(ParticleType particleType) {
            checkArgument(particleType instanceof ParticleType.Note);
            super.type(particleType);
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Note reset() {
            this.note = null;
            super.reset();
            return this;
        }

        @Override
        public LanternParticleEffect.Note build() {
            return new LanternParticleEffect.Note(this.type, this.motion, this.offset, this.count,
                    this.note == null ? ((LanternParticleType.Note) this.type).getDefaultNotePitch() : this.note);
        }
    }

    public static class Item extends AbstractParticleEffectBuilder<ItemParticle, ItemParticle.Builder> implements ItemParticle.Builder {

        private ItemStackSnapshot itemSnapshot;

        @Override
        public LanternParticleEffectBuilder.Item type(ParticleType particleType) {
            checkArgument(particleType instanceof ParticleType.Material);
            super.type(particleType);
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Item item(ItemStackSnapshot itemSnapshot) {
            this.itemSnapshot = checkNotNull(itemSnapshot, "itemSnapshot").copy();
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Item reset() {
            this.itemSnapshot = null;
            super.reset();
            return this;
        }

        @Override
        public LanternParticleEffect.Item build() {
            return new LanternParticleEffect.Item(this.type, this.motion, this.offset, this.count,
                    this.itemSnapshot == null ? ((ParticleType.Material) this.type).getDefaultItem().createSnapshot() :
                        this.itemSnapshot);
        }
    }

    public static class Block extends AbstractParticleEffectBuilder<BlockParticle, BlockParticle.Builder> implements BlockParticle.Builder {

        private BlockState blockState;

        @Override
        public LanternParticleEffectBuilder.Block type(ParticleType particleType) {
            checkArgument(particleType instanceof ParticleType.Material);
            super.type(particleType);
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Block block(BlockState blockState) {
            this.blockState = checkNotNull(blockState, "blockState");
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Block reset() {
            this.blockState = null;
            super.reset();
            return this;
        }

        @Override
        public LanternParticleEffect.Block build() {
            return new LanternParticleEffect.Block(this.type, this.motion, this.offset, this.count,
                    this.blockState == null ? ((ParticleType.Material) this.type).getDefaultItem()
                            .getItem().getBlock().get().getDefaultState() : this.blockState);
        }
    }
}
