package org.lanternpowered.server.catalog;

import org.spongepowered.api.CatalogType;

import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

public class SimpleCatalogType implements CatalogType {

    protected final String identifier;
    protected final String name;

    public SimpleCatalogType(String identifier, String name) {
        checkNotNullOrEmpty(identifier, "identifier");
        checkNotNullOrEmpty(name, "name");

        this.identifier = identifier;
        this.name = name;
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
