package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.SimpleCatalogType;

public enum LanternDirtTypes implements LanternDirtType, SimpleCatalogType {

    DIRT            ("dirt"),
    COARSE_DIRT     ("coarse_dirt"),
    PODZOL          ("podzol"),
    ;

    private final String identifier;

    LanternDirtTypes(String identifier) {
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
