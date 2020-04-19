/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.item.recipe;

import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.Recipe;

public abstract class LanternRecipe extends DefaultCatalogType implements Recipe {

    private final ItemStackSnapshot exemplaryResult;

    public LanternRecipe(CatalogKey key, ItemStackSnapshot exemplaryResult) {
        super(key);
        this.exemplaryResult = exemplaryResult;
    }

    @Override
    public ItemStackSnapshot getExemplaryResult() {
        return this.exemplaryResult;
    }
}
