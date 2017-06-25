/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.item.recipe.fuel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.plugin.PluginContainer;

@SuppressWarnings("ConstantConditions")
public class LanternFuelBuilder implements IFuel.Builder {

    private IFuelBurnTimeProvider burnTimeProvider;
    private Ingredient ingredient;

    @Override
    public IFuel.Builder ingredient(Ingredient ingredient) {
        checkNotNull(ingredient, "ingredient");
        this.ingredient = ingredient;
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

        return new LanternFuel(container.getId(), id, this.burnTimeProvider, this.ingredient);
    }
}
