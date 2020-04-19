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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.LanternArmorType;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.ArmorType;
import org.spongepowered.api.data.type.ArmorTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

public final class ArmorTypeRegistryModule extends DefaultCatalogRegistryModule<ArmorType> {

    public ArmorTypeRegistryModule() {
        super(ArmorTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternArmorType(CatalogKey.minecraft("chain")));
        register(new LanternArmorType(CatalogKey.minecraft("diamond"), () -> Ingredient.of(ItemTypes.DIAMOND)));
        register(new LanternArmorType(CatalogKey.minecraft("gold"), () -> Ingredient.of(ItemTypes.GOLD_INGOT)));
        register(new LanternArmorType(CatalogKey.minecraft("iron"), () -> Ingredient.of(ItemTypes.IRON_INGOT)));
        register(new LanternArmorType(CatalogKey.minecraft("leather"), () -> Ingredient.of(ItemTypes.LEATHER)));
    }
}
