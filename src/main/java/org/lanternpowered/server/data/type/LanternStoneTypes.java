package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleCatalogType;

public enum LanternStoneTypes implements LanternStoneType, SimpleCatalogType {

    STONE               ("stone"),
    GRANITE             ("granite"),
    GRANITE_SMOOTH      ("smooth_granite"),
    DIORITE             ("diorite"),
    DIORITE_SMOOTH      ("smooth_diorite"),
    ANDESITE            ("andesite"),
    ANDESITE_SMOOTH     ("smooth_andesite");

    private final String identifier;

    LanternStoneTypes(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    @Override
    public byte getInternalId() {
        return (byte) this.ordinal();
    }
}
