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
package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.AbstractTopBottomShiftClickBehavior;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.inventory.PlayerTopBottomContainer;
import org.lanternpowered.server.item.recipe.fuel.IFuel;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FurnaceShiftClickBehavior extends AbstractTopBottomShiftClickBehavior {

    public static final FurnaceShiftClickBehavior INSTANCE = new FurnaceShiftClickBehavior();

    @Override
    public IInventory getTarget(PlayerTopBottomContainer container, AbstractSlot slot) {
        if (container.getOpenInventory().containsInventory(slot)) {
            return getDefaultTarget(container, slot);
        }
        // The item stack should be present
        final ItemStackSnapshot snapshot = LanternItemStackSnapshot.wrap(slot.peek()); // Wrap, peek creates a copy
        // Check if the item can be used as a ingredient
        final Optional<SmeltingRecipe> optSmeltingRecipe = Lantern.getRegistry()
                .getSmeltingRecipeRegistry().findMatchingRecipe(snapshot);
        final List<IInventory> inventories = new ArrayList<>();
        final FurnaceInventory furnaceInventory = (FurnaceInventory) container.getOpenInventory();
        if (optSmeltingRecipe.isPresent()) {
            inventories.add(furnaceInventory.getInputSlot());
        }
        // Check if the item can be used as a fuel
        final Optional<IFuel> optFuel = Lantern.getRegistry()
                .getFuelRegistry().findMatching(snapshot);
        if (optFuel.isPresent()) {
            inventories.add(furnaceInventory.getFuelSlot());
        }
        return inventories.isEmpty() ? getDefaultTarget(container, slot) :
                inventories.size() == 1 ? inventories.get(0) : inventories.get(0).union(inventories.get(1));
    }
}
