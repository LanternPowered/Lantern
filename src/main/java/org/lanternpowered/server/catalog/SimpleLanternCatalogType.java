package org.lanternpowered.server.catalog;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

public class SimpleLanternCatalogType implements SimpleCatalogType {

    private final String identifier;

    public SimpleLanternCatalogType(String identifier) {
        this.identifier = checkNotNullOrEmpty(identifier, "identifier");
    }

    @Override
    public String getId() {
        return this.identifier;
    }
}
