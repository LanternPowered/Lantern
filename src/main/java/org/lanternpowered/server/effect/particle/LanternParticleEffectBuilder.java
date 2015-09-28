package org.lanternpowered.server.effect.particle;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.awt.Color;

import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import com.flowpowered.math.vector.Vector3d;

public class LanternParticleEffectBuilder implements ParticleEffectBuilder {

    protected final ParticleType type;

    protected Vector3d motion;
    protected Vector3d offset;
    protected int count;

    public LanternParticleEffectBuilder(ParticleType type) {
        this.type = checkNotNull(type, "type");
    }

    public LanternParticleEffectBuilder reset() {
        this.motion = Vector3d.ZERO;
        this.offset = Vector3d.ZERO;
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
    public LanternParticleEffect build() {
        return new LanternParticleEffect(this.type, this.motion, this.offset, this.count);
    }

    public static class Colorable extends LanternParticleEffectBuilder implements ParticleEffectBuilder.Colorable {

        private Color color;

        public Colorable(ParticleType.Colorable type) {
            super(type);
        }

        @Override
        public LanternParticleEffectBuilder.Colorable reset() {
            this.color = ((ParticleType.Colorable) this.type).getDefaultColor();
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
            return new LanternParticleEffect.Colorable(this.type, this.motion, this.offset, this.count, this.color);
        }
    }

    public static class Resizable extends LanternParticleEffectBuilder implements ParticleEffectBuilder.Resizable {

        private float size;

        public Resizable(ParticleType.Resizable type) {
            super(type);
        }

        @Override
        public LanternParticleEffectBuilder.Resizable reset() {
            this.size = ((ParticleType.Resizable) this.type).getDefaultSize();
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
            return new LanternParticleEffect.Resizable(this.type, this.motion, this.offset, this.count, this.size);
        }
    }

    public static class Note extends LanternParticleEffectBuilder implements ParticleEffectBuilder.Note {

        private float note;

        public Note(ParticleType.Note type) {
            super(type);
        }

        @Override
        public LanternParticleEffectBuilder.Note reset() {
            this.note = ((ParticleType.Note) this.type).getDefaultNote();
            return (LanternParticleEffectBuilder.Note) super.reset();
        }

        @Override
        public LanternParticleEffectBuilder.Note note(float note) {
            checkArgument(note >= 0f && note <= 24f, "The note must scale between 0 and 24!");
            this.note = note;
            return this;
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
            return new LanternParticleEffect.Note(this.type, this.motion, this.offset, this.count, this.note);
        }
    }

    public static class Material extends LanternParticleEffectBuilder implements ParticleEffectBuilder.Material {

        private ItemStack item;

        public Material(ParticleType.Material type) {
            super(type);
        }

        @Override
        public LanternParticleEffectBuilder.Material item(ItemStack item) {
            this.item = checkNotNull(item, "item").copy();
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Material itemType(ItemType item) {
            this.item = new LanternItemStack(checkNotNull(item, "item"));
            return this;
        }

        @Override
        public LanternParticleEffectBuilder.Material reset() {
            this.item = ((ParticleType.Material) this.type).getDefaultItem();
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
            return new LanternParticleEffect.Material(this.type, this.motion, this.offset, this.count, this.item);
        }
    }
}
