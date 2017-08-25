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
package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractInventorySlot;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.inventory.behavior.AbstractShiftClickBehavior;
import org.lanternpowered.server.item.recipe.fuel.IFuel;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FurnaceShiftClickBehavior extends AbstractShiftClickBehavior {

    public static final FurnaceShiftClickBehavior INSTANCE = new FurnaceShiftClickBehavior();

    @Override
    public IInventory getTarget(LanternContainer container, AbstractInventorySlot slot) {
        if (container.getOpenInventory().containsInventory(slot)) {
            return getDefaultTarget(container, slot);
        }
        // The item stack should be present
        final ItemStackSnapshot snapshot = LanternItemStackSnapshot.wrap(slot.peek().get()); // Wrap, peek creates a copy
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
