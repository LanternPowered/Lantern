package org.lanternpowered.server.catalog;

import org.spongepowered.api.CatalogType;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

public class LanternCatalogType implements CatalogType {

    protected final String identifier;
    protected final String name;

    public LanternCatalogType(String identifier, String name) {
        this.identifier = checkNotNullOrEmpty(identifier, "identifier");
        this.name = checkNotNullOrEmpty(name, "name");
    }

    @Override
    public String getId() {
        return this.identifier;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
