package org.lanternpowered.server.data.type;

import org.spongepowered.api.data.type.PlantType;

public interface LanternPlantType extends PlantType {

    FlowerColor getFlowerColor();

    byte getInternalId();

    public static enum FlowerColor {
        YELLOW,
        RED,
        ;
    }
}
