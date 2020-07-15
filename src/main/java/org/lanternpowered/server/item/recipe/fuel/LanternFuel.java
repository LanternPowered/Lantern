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
package org.lanternpowered.server.item.recipe.fuel;

import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.OptionalInt;

final class LanternFuel extends DefaultCatalogType implements IFuel {

    final IFuelBurnTimeProvider burnTimeProvider;
    private final IIngredient ingredient;

    LanternFuel(ResourceKey key,
            IFuelBurnTimeProvider burnTimeProvider,
            IIngredient ingredient) {
        super(key);
        this.burnTimeProvider = burnTimeProvider;
        this.ingredient = ingredient;
    }

    @Override
    public IIngredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public OptionalInt getBurnTime(ItemStackSnapshot input) {
        return isValid(input) ? OptionalInt.of(this.burnTimeProvider.get(input)) : OptionalInt.empty();
    }

    @Override
    public OptionalInt getBurnTime(ItemStack input) {
        return isValid(input) ? OptionalInt.of(this.burnTimeProvider.get(input)) : OptionalInt.empty();
    }
}
