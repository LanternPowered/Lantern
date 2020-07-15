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
package org.lanternpowered.server.item.recipe.crafting;

import org.lanternpowered.server.item.recipe.LanternRecipe;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

abstract class LanternCraftingRecipe extends LanternRecipe implements ISimpleCraftingRecipe {

    @Nullable private final String group;

    LanternCraftingRecipe(ResourceKey key, ItemStackSnapshot exemplaryResult, @Nullable String group) {
        super(key, exemplaryResult);
        this.group = group;
    }

    @Override
    public Optional<String> getGroup() {
        return Optional.ofNullable(this.group);
    }
}
