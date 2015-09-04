package org.lanternpowered.server.effect.particle;

import java.awt.Color;

import org.lanternpowered.server.catalog.LanternSimpleCatalogType;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.item.inventory.ItemStack;

public class LanternParticleType extends LanternSimpleCatalogType implements ParticleType {

    private final boolean hasMotion;
    private final int internalId;

    public LanternParticleType(String identifier, int internalId, boolean hasMotion) {
        super(identifier);
        this.internalId = internalId;
        this.hasMotion = hasMotion;
    }

    @Override
    public boolean hasMotion() {
        return this.hasMotion;
    }

    public int getInternalId() {
        return this.internalId;
    }

    public static class Material extends LanternParticleType implements ParticleType.Material {

        private final ItemStack defaultItem;

        public Material(String name, int internalId, boolean hasMotion, ItemStack defaultItem) {
            super(name, internalId, hasMotion);
            this.defaultItem = defaultItem;
        }

        @Override
        public ItemStack getDefaultItem() {
            return this.defaultItem.copy();
        }
    }

    public static class Colorable extends LanternParticleType implements ParticleType.Colorable {

        private final Color defaultColor;

        public Colorable(String name, int internalId, boolean hasMotion, Color defaultColor) {
            super(name, internalId, hasMotion);
            this.defaultColor = defaultColor;
        }

        @Override
        public Color getDefaultColor() {
            return this.defaultColor;
        }
    }

    public static class Resizable extends LanternParticleType implements ParticleType.Resizable {

        private final float defaultSize;

        public Resizable(String name, int internalId, boolean hasMotion, float defaultSize) {
            super(name, internalId, hasMotion);
            this.defaultSize = defaultSize;
        }

        @Override
        public float getDefaultSize() {
            return this.defaultSize;
        }
    }

    public static class Note extends LanternParticleType implements ParticleType.Note {

        private final float defaultNote;

        public Note(String name, int internalId, boolean hasMotion, float defaultNote) {
            super(name, internalId, hasMotion);
            this.defaultNote = defaultNote;
        }

        @Override
        public float getDefaultNote() {
            return this.defaultNote;
        }
    }
}
