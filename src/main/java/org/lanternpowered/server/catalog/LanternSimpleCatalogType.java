package org.lanternpowered.server.catalog;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

public class LanternSimpleCatalogType implements SimpleCatalogType {

    private final String identifier;

    public LanternSimpleCatalogType(String identifier) {
        this.identifier = checkNotNullOrEmpty(identifier, "identifier");
    }

    @Override
    public String getId() {
        return this.identifier;
    }
}
