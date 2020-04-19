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
