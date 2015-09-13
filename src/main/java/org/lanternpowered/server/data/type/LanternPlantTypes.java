package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleCatalogType;

public enum LanternPlantTypes implements LanternPlantType, SimpleCatalogType {

    DANDELION           (FlowerColor.YELLOW, 0, "dandelion"),
    POPPY               (FlowerColor.RED, 0, "poppy"),
    BLUE_ORCHID         (FlowerColor.RED, 1, "blue_orchid"),
    ALLIUM              (FlowerColor.RED, 2, "allium"),
    HOUSTONIA           (FlowerColor.RED, 3, "houstonia"),
    RED_TULIP           (FlowerColor.RED, 4, "red_tulip"),
    ORANGE_TULIP        (FlowerColor.RED, 5, "orange_tulip"),
    WHITE_TULIP         (FlowerColor.RED, 6, "white_tulip"),
    PINK_TULIP          (FlowerColor.RED, 7, "pink_tulip"),
    OXEYE_DAISY         (FlowerColor.RED, 8, "oxeye_daisy")
    ;

    private final FlowerColor flowerColor;
    private final String identifier;

    private final byte internalId;

    LanternPlantTypes(FlowerColor flowerColor, int internalId, String identifier) {
        this.internalId = (byte) internalId;
        this.flowerColor = flowerColor;
        this.identifier = identifier;
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    @Override
    public FlowerColor getFlowerColor() {
        return this.flowerColor;
    }

    @Override
    public byte getInternalId() {
        return this.internalId;
    }
}
