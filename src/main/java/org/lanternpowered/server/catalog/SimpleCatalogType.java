package org.lanternpowered.server.catalog;

import org.spongepowered.api.CatalogType;

public interface SimpleCatalogType extends CatalogType {

    @Override
    default String getName() {
        return this.getId();
    }
}
