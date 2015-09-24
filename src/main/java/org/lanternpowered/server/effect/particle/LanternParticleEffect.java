package org.lanternpowered.server.effect.particle;

import java.awt.Color;

import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.item.inventory.ItemStack;

import com.flowpowered.math.vector.Vector3d;

public class LanternParticleEffect implements ParticleEffect {

    private final ParticleType type;
    private final Vector3d motion;
    private final Vector3d offset;
    private final int count;

    LanternParticleEffect(ParticleType type, Vector3d motion, Vector3d offset, int count) {
        this.motion = motion;
        this.offset = offset;
        this.count = count;
        this.type = type;
    }

    @Override
    public ParticleType getType() {
        return this.type;
    }

    @Override
    public Vector3d getMotion() {
        return this.motion;
    }

    @Override
    public Vector3d getOffset() {
        return this.offset;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    public static class Colorable extends LanternParticleEffect implements ParticleEffect.Colorable {

        private final Color color;

        Colorable(ParticleType type, Vector3d motion, Vector3d offset, int count, Color color) {
            super(type, motion, offset, count);
            this.color = color;
        }

        @Override
        public Color getColor() {
            return this.color;
        }
    }

    public static class Resizable extends LanternParticleEffect implements ParticleEffect.Resizable {

        private final float size;

        Resizable(ParticleType type, Vector3d motion, Vector3d offset, int count, float size) {
            super(type, motion, offset, count);
            this.size = size;
        }

        @Override
        public float getSize() {
            return this.size;
        }
    }

    public static class Note extends LanternParticleEffect implements ParticleEffect.Note {

        private final float note;

        Note(ParticleType type, Vector3d motion, Vector3d offset, int count, float note) {
            super(type, motion, offset, count);
            this.note = note;
        }

        @Override
        public float getNote() {
            return this.note;
        }
    }

    public static class Material extends LanternParticleEffect implements ParticleEffect.Material {

        private final ItemStack item;

        Material(ParticleType type, Vector3d motion, Vector3d offset, int count, ItemStack item) {
            super(type, motion, offset, count);
            this.item = item;
        }

        @Override
        public ItemStack getItem() {
            return this.item.copy();
        }
    }
}
