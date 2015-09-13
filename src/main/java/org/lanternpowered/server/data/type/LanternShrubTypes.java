package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleCatalogType;

public enum LanternShrubTypes implements LanternShrubType, SimpleCatalogType {

    DEAD_BUSH       ("dead_bush"),
    GRASS           ("tall_grass"),
    FERN            ("fern");
    ;

    private final String identifier;

    LanternShrubTypes(String identifier) {
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
