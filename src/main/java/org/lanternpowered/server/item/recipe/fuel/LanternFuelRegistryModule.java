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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.util.DelayedRegistration;

import java.util.Optional;
import java.util.OptionalInt;

public class LanternFuelRegistryModule extends DefaultCatalogRegistryModule<IFuel> implements IFuelRegistry {

    public LanternFuelRegistryModule() {
        super();
    }

    @Override
    public <A extends IFuel> A register(A fuel) {
        return super.register(fuel);
    }

    @Override
    public Optional<IFuel> findMatching(ItemStackSnapshot ingredient) {
        checkNotNull(ingredient, "ingredient");
        for (IFuel fuel : getAll()) {
            if (fuel.isValid(ingredient)) {
                return Optional.of(fuel);
            }
        }
        return Optional.empty();
    }

    @Override
    public OptionalInt getResult(ItemStackSnapshot ingredient) {
        checkNotNull(ingredient, "ingredient");
        for (IFuel fuel : getAll()) {
            final OptionalInt result = fuel.getBurnTime(ingredient);
            if (result.isPresent()) {
                return result;
            }
        }
        return OptionalInt.empty();
    }

    @DelayedRegistration(RegistrationPhase.POST_INIT)
    @Override
    public void registerDefaults() {
        final PluginContainer plugin = Lantern.getMinecraftPlugin();
        register(IFuel.builder()
                .ingredient(IIngredient.builder().with(ItemTypes.LAVA_BUCKET).withRemaining(ItemTypes.BUCKET).build())
                .burnTime(20000)
                .build("lava_bucket", plugin));
        register(IFuel.builder()
                .ingredient(Ingredient.of(ItemTypes.COAL))
                .burnTime(1600)
                .build("coal", plugin));
    }
}
