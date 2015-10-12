package org.lanternpowered.server.catalog;

import org.spongepowered.api.CatalogType;

public interface PluginCatalogType extends CatalogType {

    /**
     * {@inheritDoc}
     */
    @Override
    default String getId() {
        return this.getPluginId() + ':' + this.getName();
    }

    /**
     * Gets the identifier of the plugin that
     * owns the catalog type.
     * 
     * @return the plugin identifier
     */
    String getPluginId();

}
