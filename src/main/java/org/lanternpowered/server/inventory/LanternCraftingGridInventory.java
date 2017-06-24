package org.lanternpowered.server.inventory;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.world.World;

import java.util.Optional;

import javax.annotation.Nullable;

public class LanternCraftingGridInventory extends LanternGridInventory implements CraftingGridInventory {

    public LanternCraftingGridInventory(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    public LanternCraftingGridInventory(@Nullable Inventory parent) {
        super(parent);
    }

    @Override
    public Optional<CraftingRecipe> getRecipe(World world) {
        return Lantern.getRegistry().getCraftingRecipeRegistry().findMatchingRecipe(this, world);
    }
}
