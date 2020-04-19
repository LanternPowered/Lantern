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

import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.item.recipe.RecipeRegistry;

public abstract class AbstractRecipeRegistry<T extends Recipe> extends DefaultCatalogRegistryModule<T> implements RecipeRegistry<T> {

    public AbstractRecipeRegistry(Class<?>... catalogClasses) {
        super(catalogClasses);
    }

    @Override
    protected <A extends T> A register(A catalogType) {
        return super.register(catalogType);
    }
}
