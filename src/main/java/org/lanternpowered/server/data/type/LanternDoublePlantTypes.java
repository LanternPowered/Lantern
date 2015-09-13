package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleCatalogType;

public enum LanternDoublePlantTypes implements LanternDoublePlantType, SimpleCatalogType {

    SUNFLOWER       ("sunflower"),
    SYRINGA         ("syringa"),
    GRASS           ("double_grass"),
    FERN            ("double_fern"),
    ROSE            ("double_rose"),
    PAEONIA         ("paeonia")
    ;

    private final String identifier;

    LanternDoublePlantTypes(String identifier) {
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
