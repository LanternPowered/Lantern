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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.plugin.PluginContainer;

@SuppressWarnings("ConstantConditions")
public class LanternFuelBuilder implements IFuel.Builder {

    private IFuelBurnTimeProvider burnTimeProvider;
    private IIngredient ingredient;

    @Override
    public IFuel.Builder ingredient(Ingredient ingredient) {
        checkNotNull(ingredient, "ingredient");
        this.ingredient = (IIngredient) ingredient;
        return this;
    }

    @Override
    public IFuel.Builder ingredient(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        this.ingredient = IIngredient.builder().with(itemType).build();
        return this;
    }

    @Override
    public IFuel.Builder ingredient(ItemStack itemStack) {
        checkNotNull(itemStack, "itemStack");
        this.ingredient = IIngredient.builder().with(itemStack).build();
        return this;
    }

    @Override
    public IFuel.Builder ingredient(ItemStackSnapshot itemStackSnapshot) {
        checkNotNull(itemStackSnapshot, "itemStackSnapshot");
        this.ingredient = IIngredient.builder().with(itemStackSnapshot).build();
        return this;
    }

    @Override
    public IFuel.Builder burnTime(IFuelBurnTimeProvider provider) {
        checkNotNull(provider, "provider");
        this.burnTimeProvider = provider;
        return this;
    }

    @Override
    public IFuel.Builder burnTime(int ticks) {
        checkArgument(ticks > 0, "The burn ticks must be greater then 0");
        this.burnTimeProvider = new ConstantFuelBurnTimeProvider(ticks);
        return this;
    }

    @Override
    public IFuel.Builder from(IFuel value) {
        this.burnTimeProvider = ((LanternFuel) value).burnTimeProvider;
        this.ingredient = value.getIngredient();
        return this;
    }

    @Override
    public IFuel.Builder reset() {
        this.burnTimeProvider = null;
        this.ingredient = null;
        return this;
    }

    @Override
    public IFuel build(String id, Object plugin) {
        checkState(this.burnTimeProvider != null, "The burn time provider is not set.");
        checkState(this.ingredient != null, "The ingredient is not set.");
        checkNotNull(id, "id");
        checkNotNull(plugin, "plugin");

        final PluginContainer container = checkPlugin(plugin, "plugin");
        final int index = id.indexOf(':');
        if (index != -1) {
            final String pluginId = id.substring(0, index);
            checkState(pluginId.equals(container.getId()), "Plugin ids mismatch, "
                    + "found %s in the id but got %s from the container", pluginId, container.getId());
            id = id.substring(index + 1);
        }

        return new LanternFuel(ResourceKey.of(container.getId(), id), this.burnTimeProvider, this.ingredient);
    }
}
