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
package org.lanternpowered.server.inventory.type;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.AbstractOrderedInventory;
import org.lanternpowered.server.inventory.CarrierReference;
import org.spongepowered.api.item.inventory.Carrier;
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

import javax.annotation.Nullable;

public class LanternCraftingInventory extends AbstractOrderedInventory implements CraftingInventory {

    private static final class Holder {

        private static final QueryOperation<?> CRAFTING_OUTPUT_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(CraftingOutput.class);
        private static final QueryOperation<?> CRAFTING_GRID_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(CraftingGridInventory.class);
    }

    protected final CarrierReference<?> carrierReference;

    private CraftingOutput output;
    private CraftingGridInventory grid;

    protected LanternCraftingInventory(CarrierReference<?> carrierReference) {
        this.carrierReference = carrierReference;
    }

    public LanternCraftingInventory() {
        this(CarrierReference.of(Locatable.class));
    }

    @Override
    protected void init() {
        super.init();

        // Search for the underlying inventories
        this.output = query(Holder.CRAFTING_OUTPUT_OPERATION).first();
        this.grid = query(Holder.CRAFTING_GRID_OPERATION).first();

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

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        this.carrierReference.set(carrier);
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
        final Optional<Locatable> optLocatable = this.carrierReference.as(Locatable.class);
        if (optLocatable.isPresent()) {
            return optLocatable.get().getWorld();
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
