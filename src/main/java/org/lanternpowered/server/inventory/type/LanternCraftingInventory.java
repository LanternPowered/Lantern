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
package org.lanternpowered.server.inventory.type;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.ICarriedInventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.World;

import java.util.Iterator;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternCraftingInventory extends AbstractChildrenInventory implements CraftingInventory {

    private static final class Holder {

        private static final QueryOperation<?> CRAFTING_OUTPUT_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(CraftingOutput.class);
        private static final QueryOperation<?> CRAFTING_GRID_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(CraftingGridInventory.class);
    }

    private CraftingOutput output;
    private CraftingGridInventory grid;

    @Override
    protected void init() {
        super.init();

        // Search for the underlying inventories
        this.output = (CraftingOutput) query(Holder.CRAFTING_OUTPUT_OPERATION).first();
        this.grid = (CraftingGridInventory) query(Holder.CRAFTING_GRID_OPERATION).first();

        // Add a change listener to update the output slot
        ((AbstractInventory) this.grid).addChangeListener(slot -> {
            final World world = getWorld();
            if (world == null) {
                return;
            }
            final Optional<CraftingResult> optResult = Lantern.getRegistry()
                    .getCraftingRecipeRegistry().getResult(this.grid, getWorld());
            this.output.set(optResult
                    .map(CraftingResult::getResult)
                    .map(ItemStackSnapshot::createStack)
                    .orElse(ItemStack.empty()));
        });
    }

    /**
     * Attempts to get a {@link World} instance that is passed through in the
     * crafting system. By default, lets just use the first {@link World} that
     * we can find.
     *
     * @return The world
     */
    @SuppressWarnings("unchecked")
    @Nullable
    protected World getWorld() {
        if (this instanceof ICarriedInventory) {
            final Optional<Locatable> optLocatable = ((ICarriedInventory) this).getCarrierAs(Locatable.class);
            if (optLocatable.isPresent()) {
                return optLocatable.get().getWorld();
            }
        }
        final Iterator<World> it = Lantern.getWorldManager().getWorlds().iterator();
        return it.hasNext() ? it.next() : null;
    }

    @Override
    public CraftingGridInventory getCraftingGrid() {
        return this.grid;
    }

    @Override
    public CraftingOutput getResult() {
        return this.output;
    }
}
