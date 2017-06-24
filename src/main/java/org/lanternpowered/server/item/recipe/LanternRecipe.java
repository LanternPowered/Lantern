package org.lanternpowered.server.item.recipe;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.Recipe;

public abstract class LanternRecipe extends PluginCatalogType.Base implements Recipe {

    private final ItemStackSnapshot exemplaryResult;

    public LanternRecipe(String pluginId, String name, ItemStackSnapshot exemplaryResult) {
        super(pluginId, name);
        this.exemplaryResult = exemplaryResult;
    }

    public LanternRecipe(String pluginId, String id, String name, ItemStackSnapshot exemplaryResult) {
        super(pluginId, id, name);
        this.exemplaryResult = exemplaryResult;
    }

    @Override
    public ItemStackSnapshot getExemplaryResult() {
        return this.exemplaryResult;
    }
}
