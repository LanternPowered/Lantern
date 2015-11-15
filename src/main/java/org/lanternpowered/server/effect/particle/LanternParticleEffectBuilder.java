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

import java.awt.Color;

import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.ColoredParticle;
import org.spongepowered.api.effect.particle.ItemParticle;
import org.spongepowered.api.effect.particle.NoteParticle;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ResizableParticle;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import com.flowpowered.math.vector.Vector3d;

public class LanternParticleEffectBuilder implements ParticleEffect.Builder {

    protected ParticleType type;

    protected Vector3d motion;
    protected Vector3d offset;
    protected int count;

    public LanternParticleEffectBuilder() {
        this.reset();
    }

    public LanternParticleEffectBuilder reset() {
        this.motion = Vector3d.ZERO;
        this.offset = Vector3d.ZERO;
        this.type = null;
        this.count = 1;
        return this;
    }

    @Override
    public LanternParticleEffectBuilder motion(Vector3d motion) {
        this.motion = checkNotNull(motion, "motion");
        return this;
    }

    @Override
    public LanternParticleEffectBuilder offset(Vector3d offset) {
        this.offset = checkNotNull(offset, "offset");
        return this;
    }

    @Override
    public LanternParticleEffectBuilder count(int count) throws IllegalArgumentException {
        checkArgument(count > 0, "Count must be greater then zero!");
        this.count = count;
        return null;
    }

    @Override
    public LanternParticleEffectBuilder type(ParticleType particleType) {
        this.type = checkNotNull(particleType, "particleType");
        return this;
    }

    @Override
    public LanternParticleEffect build() {
        return new LanternParticleEffect(this.type, this.motion, this.offset, this.count);
    }

    public static class Colorable extends LanternParticleEffectBuilder implements ColoredParticle.Builder {

        private Color color;

        @Override
        public LanternParticleEffectBuilder.Colorable type(ParticleType particleType) {
            checkArgument(particleType instanceof ParticleType.Colorable);
            return (LanternParticleEffectBuilder.Colorable) super.type(particleType);
        }

        @Override
        public LanternParticleEffectBuilder.Colorable reset() {
            this.color = null;
            return (LanternParticleEffectBuilder.Colorable) super.reset();
        }

        @Override
        public LanternParticleEffectBuilder.Colorable color(Color color) {
            this.color = checkNotNull(color, "color");
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Colorable motion(Vector3d motion) {
            return (LanternParticleEffectBuilder.Colorable) super.motion(motion);
        }

        @Override
        public LanternParticleEffectBuilder.Colorable offset(Vector3d offset) {
            return (LanternParticleEffectBuilder.Colorable) super.offset(offset);
        }

        @Override
        public LanternParticleEffectBuilder.Colorable count(int count) {
            return (LanternParticleEffectBuilder.Colorable) super.count(count);
        }

        @Override
        public LanternParticleEffect.Colorable build() {
            return new LanternParticleEffect.Colorable(this.type, this.motion, this.offset, this.count,
                    this.color == null ? ((ParticleType.Colorable) this.type).getDefaultColor() : this.color);
        }
    }

    public static class Resizable extends LanternParticleEffectBuilder implements ResizableParticle.Builder {

        private Float size;

        @Override
        public LanternParticleEffectBuilder.Resizable type(ParticleType particleType) {
            checkArgument(particleType instanceof ParticleType.Resizable);
            return (LanternParticleEffectBuilder.Resizable) super.type(particleType);
        }

        @Override
        public LanternParticleEffectBuilder.Resizable reset() {
            this.size = null;
            return (LanternParticleEffectBuilder.Resizable) super.reset();
        }

        @Override
        public LanternParticleEffectBuilder.Resizable size(float size) {
            this.size = size;
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Resizable motion(Vector3d motion) {
            return (LanternParticleEffectBuilder.Resizable) super.motion(motion);
        }

        @Override
        public LanternParticleEffectBuilder.Resizable offset(Vector3d offset) {
            return (LanternParticleEffectBuilder.Resizable) super.offset(offset);
        }

        @Override
        public LanternParticleEffectBuilder.Resizable count(int count) {
            return (LanternParticleEffectBuilder.Resizable) super.count(count);
        }

        @Override
        public LanternParticleEffect.Resizable build() {
            return new LanternParticleEffect.Resizable(this.type, this.motion, this.offset, this.count,
                    this.size == null ? ((ParticleType.Resizable) this.type).getDefaultSize() : this.size);
        }
    }

    public static class Note extends LanternParticleEffectBuilder implements NoteParticle.Builder {

        private NotePitch note;

        @Override
        public LanternParticleEffectBuilder.Note note(NotePitch note) {
            this.note = checkNotNull(note, "note");
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Note type(ParticleType particleType) {
            checkArgument(particleType instanceof ParticleType.Note);
            return (LanternParticleEffectBuilder.Note) super.type(particleType);
        }

        @Override
        public LanternParticleEffectBuilder.Note reset() {
            this.note = null;
            return (LanternParticleEffectBuilder.Note) super.reset();
        }

        @Override
        public LanternParticleEffectBuilder.Note motion(Vector3d motion) {
            return (LanternParticleEffectBuilder.Note) super.motion(motion);
        }

        @Override
        public LanternParticleEffectBuilder.Note offset(Vector3d offset) {
            return (LanternParticleEffectBuilder.Note) super.offset(offset);
        }

        @Override
        public LanternParticleEffectBuilder.Note count(int count) {
            return (LanternParticleEffectBuilder.Note) super.count(count);
        }

        @Override
        public LanternParticleEffect.Note build() {
            return new LanternParticleEffect.Note(this.type, this.motion, this.offset, this.count,
                    this.note == null ? ((LanternParticleType.Note) this.type).getDefaultNotePitch() : this.note);
        }
    }

    public static class Material extends LanternParticleEffectBuilder implements ItemParticle.Builder {

        private ItemStackSnapshot itemSnapshot;

        @Override
        public LanternParticleEffectBuilder.Material type(ParticleType particleType) {
            checkArgument(particleType instanceof ParticleType.Material);
            return (LanternParticleEffectBuilder.Material) super.type(particleType);
        }

        @Override
        public LanternParticleEffectBuilder.Material item(ItemStackSnapshot itemSnapshot) {
            this.itemSnapshot = checkNotNull(itemSnapshot, "itemSnapshot").copy();
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Material reset() {
            this.itemSnapshot = null;
            return (LanternParticleEffectBuilder.Material) super.reset();
        }

        @Override
        public LanternParticleEffectBuilder.Material motion(Vector3d motion) {
            return (LanternParticleEffectBuilder.Material) super.motion(motion);
        }

        @Override
        public LanternParticleEffectBuilder.Material offset(Vector3d offset) {
            return (LanternParticleEffectBuilder.Material) super.offset(offset);
        }

        @Override
        public LanternParticleEffectBuilder.Material count(int count) {
            return (LanternParticleEffectBuilder.Material) super.count(count);
        }

        @Override
        public LanternParticleEffect.Material build() {
            return new LanternParticleEffect.Material(this.type, this.motion, this.offset, this.count,
                    this.itemSnapshot == null ? ((ParticleType.Material) this.type).getDefaultItem().createSnapshot() :
                        this.itemSnapshot);
        }
    }
}
