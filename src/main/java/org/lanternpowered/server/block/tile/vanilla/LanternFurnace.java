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
package org.lanternpowered.server.block.tile.vanilla;

import org.lanternpowered.server.block.tile.ITileEntityInventory;
import org.lanternpowered.server.block.tile.ITileEntityRefreshBehavior;
import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.element.ElementListener;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.inventory.LanternOrderedInventory;
import org.lanternpowered.server.inventory.PeekOfferTransactionsResult;
import org.lanternpowered.server.inventory.VanillaOpenableInventory;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ContainerProperties;
import org.lanternpowered.server.inventory.client.FurnaceClientContainer;
import org.lanternpowered.server.inventory.entity.HumanInventoryView;
import org.lanternpowered.server.inventory.slot.LanternFuelSlot;
import org.lanternpowered.server.inventory.slot.LanternInputSlot;
import org.lanternpowered.server.inventory.slot.LanternOutputSlot;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.lanternpowered.server.item.recipe.fuel.IFuel;
import org.lanternpowered.server.item.recipe.smelting.ISmeltingRecipe;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.carrier.Furnace;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.slot.InputSlot;
import org.spongepowered.api.item.inventory.type.TileEntityInventory;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import javax.annotation.Nullable;

public class LanternFurnace extends LanternTileEntity implements Furnace, ITileEntityRefreshBehavior {

    private class FurnaceInventory extends LanternOrderedInventory implements ITileEntityInventory, VanillaOpenableInventory {

        private final LanternInputSlot inputSlot;
        private final LanternFuelSlot fuelSlot;
        private final LanternOutputSlot outputSlot;

        private double smeltProgress;
        private double fuelProgress;

        FurnaceInventory(@Nullable Inventory parent, @Nullable Translation name) {
            super(parent, name);

            registerSlot(this.inputSlot = new LanternInputSlot(this));
            registerSlot(this.fuelSlot = new LanternFuelSlot(this, stack ->
                    Lantern.getRegistry().getFuelRegistry().findMatching(stack.createSnapshot()).isPresent()));
            registerSlot(this.outputSlot = new LanternOutputSlot(this));

            finalizeContent();
        }

        @Override
        public Optional<TileEntityCarrier> getTileEntity() {
            return Optional.of(LanternFurnace.this);
        }

        @Override
        public IInventory getShiftClickTarget(LanternContainer container, Slot slot) {
            // Use the default behavior in case the slot is located in this inventory
            if (isChild(slot)) {
                if (slot instanceof InputSlot) {
                    // The input slots uses a different insertion order to the default
                    return container.getPlayerInventory().getInventoryView(HumanInventoryView.PRIORITY_MAIN_AND_HOTBAR);
                }
                return VanillaOpenableInventory.super.getShiftClickTarget(container, slot);
            }
            // The item stack should be present
            final ItemStackSnapshot snapshot = LanternItemStackSnapshot.wrap(slot.peek().get()); // Wrap, peek creates a copy
            // Check if the item can be used as a ingredient
            final Optional<SmeltingRecipe> optSmeltingRecipe = Lantern.getRegistry()
                    .getSmeltingRecipeRegistry().findMatchingRecipe(snapshot);
            final List<IInventory> inventories = new ArrayList<>();
            if (optSmeltingRecipe.isPresent()) {
                inventories.add(this.inputSlot);
            }
            // Check if the item can be used as a fuel
            final Optional<IFuel> optFuel = Lantern.getRegistry()
                    .getFuelRegistry().findMatching(snapshot);
            if (optFuel.isPresent()) {
                inventories.add(this.fuelSlot);
            }
            return inventories.isEmpty() ? VanillaOpenableInventory.super.getShiftClickTarget(container, slot) :
                    inventories.size() == 1 ? inventories.get(0) : inventories.get(0).union(inventories.get(1));
        }

        @Override
        public boolean disableShiftClickWhenFull() {
            return false;
        }

        @Override
        public ClientContainer constructClientContainer0(LanternContainer container) {
            final FurnaceClientContainer clientContainer = new FurnaceClientContainer(TextTranslation.toText(getName()));
            // Provide the smelting progress
            clientContainer.bindPropertySupplier(ContainerProperties.SMELT_PROGRESS, () -> {
                double smeltProgress = this.smeltProgress;
                if (smeltProgress < 0) {
                    smeltProgress = this.smeltProgress = get(Keys.PASSED_COOK_TIME).get().doubleValue() / get(Keys.MAX_COOK_TIME).get().doubleValue();
                }
                return smeltProgress;
            });
            // Provide the fuel progress
            clientContainer.bindPropertySupplier(ContainerProperties.FUEL_PROGRESS, () -> {
                double fuelProgress = this.fuelProgress;
                if (fuelProgress < 0) {
                    fuelProgress = this.fuelProgress = 1.0 - get(Keys.PASSED_BURN_TIME).get().doubleValue() / get(Keys.MAX_BURN_TIME).get().doubleValue();
                }
                return fuelProgress;
            });
            return clientContainer;
        }
    }

    // The inventory of the furnace
    private final FurnaceInventory inventory = new FurnaceInventory(null, null);

    // The tick since the last pulse
    private long lastTick = -1;

    @Override
    public void registerKeys() {
        super.registerKeys();

        final ValueCollection c = getValueCollection();
        final ElementListener<Integer> clearProperty = (oldElement, newElement) -> {
            this.inventory.smeltProgress = -1;
            this.inventory.fuelProgress = -1;
        };
        c.register(Keys.MAX_BURN_TIME, 0, 0, Integer.MAX_VALUE).addListener(clearProperty);
        c.register(Keys.PASSED_BURN_TIME, 0, 0, Keys.MAX_BURN_TIME).addListener(clearProperty);
        c.register(Keys.MAX_COOK_TIME, 0, 0, Integer.MAX_VALUE).addListener(clearProperty);
        c.register(Keys.PASSED_COOK_TIME, 0, 0, Keys.MAX_COOK_TIME).addListener(clearProperty);
    }

    @Override
    public BlockState getBlock() {
        final BlockState block = getLocation().getBlock();
        return block.getType() == BlockTypes.FURNACE || block.getType() == BlockTypes.LIT_FURNACE ? block :
                BlockTypes.FURNACE.getDefaultState();
    }

    @Override
    public boolean shouldRefresh(BlockState oldBlockState, BlockState newBlockState) {
        final BlockType n = oldBlockState.getType();
        final BlockType o = newBlockState.getType();
        return !((n == BlockTypes.FURNACE || n == BlockTypes.LIT_FURNACE) && (o == BlockTypes.FURNACE || o == BlockTypes.LIT_FURNACE));
    }

    @Override
    public void pulse() {
        super.pulse();

        if (this.lastTick == -1) {
            this.lastTick = LanternGame.currentTimeTicks();
            return;
        }

        final long ticks = LanternGame.currentTimeTicks();
        long elapsed = ticks - this.lastTick;

        // This shouldn't happen
        if (elapsed == 0) {
            return;
        }
        this.lastTick = ticks;

        while (elapsed > 0) {
            int maxCookTime = 0;

            Optional<SmeltingResult> smeltingResult = null;
            Optional<SmeltingRecipe> smeltingRecipe = null;
            ItemStack itemStack = this.inventory.inputSlot.getRawItemStack();
            final ItemStackSnapshot inputSlotItemSnapshot = itemStack == null ? null : itemStack.createSnapshot();
            if (inputSlotItemSnapshot != null) {
                // Check if the item can be smelted, this means finding a compatible
                // recipe and the output has to be empty.
                smeltingRecipe = Lantern.getRegistry().getSmeltingRecipeRegistry()
                        .findMatchingRecipe(inputSlotItemSnapshot);
                if (smeltingRecipe.isPresent()) {
                    final int quantity = ((ISmeltingRecipe) smeltingRecipe.get()).getIngredient().getQuantity(inputSlotItemSnapshot);
                    if (inputSlotItemSnapshot.getQuantity() >= quantity) {
                        smeltingResult = smeltingRecipe.get().getResult(inputSlotItemSnapshot);
                        // Check if the item can be smelted
                        if (smeltingResult.isPresent()) {
                            // Check if the result could be added to the output
                            final PeekOfferTransactionsResult peekResult = this.inventory.outputSlot.peekOfferFastTransactions(
                                    smeltingResult.get().getResult().createStack());
                            if (peekResult.getOfferResult().isSuccess()) {
                                maxCookTime = ((ISmeltingRecipe) smeltingRecipe.get())
                                        .getSmeltTime(inputSlotItemSnapshot).orElse(200);
                            }
                        }
                    }
                }
            }

            // The ticks that are elapsed in this loop, limit
            // this to one cooking cycle, this can only happen
            // if actually a item is being cooked
            long elapsed1 = elapsed;

            int elapsedCookTime = get(Keys.PASSED_COOK_TIME).get();
            int remainingCookTime = maxCookTime - elapsedCookTime;

            if (maxCookTime > 0 && elapsed1 > remainingCookTime) {
                elapsed1 = remainingCookTime;
            }
            elapsed -= elapsed1;

            // Burn items until the furnace is burning properly
            int maxBurnTime = get(Keys.MAX_BURN_TIME).get();
            int elapsedBurnTime = get(Keys.PASSED_BURN_TIME).get();
            int remainingBurnTime = maxBurnTime - elapsedBurnTime;

            long elapsed2 = elapsed1;
            while (elapsed2 >= remainingBurnTime) {
                elapsed2 -= remainingBurnTime;
                // Reset the max burn time
                maxBurnTime = 0;
                // Only burn a new item if the target item can be smelted
                itemStack = this.inventory.fuelSlot.getRawItemStack();
                if (itemStack != null && maxCookTime > 0) {
                    // Check for the next fuel item
                    final ItemStackSnapshot itemStackSnapshot = itemStack.createSnapshot();
                    final Optional<IFuel> result = Lantern.getRegistry().getFuelRegistry().findMatching(itemStackSnapshot);
                    if (result.isPresent()) {
                        final OptionalInt optBurnTime = result.get().getBurnTime(itemStackSnapshot);
                        // We have a next matching burn item, check if we can poll one and then continue burning
                        if (optBurnTime.isPresent() && this.inventory.fuelSlot.poll(1).isPresent()) {
                            maxBurnTime = optBurnTime.getAsInt();
                            remainingBurnTime = maxBurnTime;
                            elapsedBurnTime = 0;
                            // Put the rest item in the slot, if the slot is empty
                            if (this.inventory.fuelSlot.size() == 0) {
                                final IIngredient ingredient = result.get().getIngredient();
                                final Optional<ItemStack> remainingItem = ingredient.getRemainingItem(itemStackSnapshot);
                                remainingItem.ifPresent(this.inventory.fuelSlot::set);
                            }
                        }
                    }
                }
                if (maxBurnTime == 0) {
                    break;
                }
            }

            elapsedBurnTime = maxBurnTime == 0 ? 0 : (int) (elapsedBurnTime + elapsed2);
            remainingBurnTime = maxBurnTime - elapsedBurnTime;

            offer(Keys.MAX_BURN_TIME, maxBurnTime);
            offer(Keys.PASSED_BURN_TIME, elapsedBurnTime);

            if (maxCookTime > 0) {
                // The furnace is still burning
                if (remainingBurnTime > 0) {
                    // The item is smelted
                    if (elapsed1 >= remainingCookTime){
                        offer(Keys.MAX_COOK_TIME, 0);
                        offer(Keys.PASSED_COOK_TIME, 0);

                        final int quantity = ((ISmeltingRecipe) smeltingRecipe.get()).getIngredient().getQuantity(inputSlotItemSnapshot);
                        this.inventory.outputSlot.offer(smeltingResult.get().getResult().createStack());
                        this.inventory.inputSlot.poll(quantity);

                        // Put the rest item in the slot
                        if (this.inventory.inputSlot.size() == 0) {
                            final IIngredient ingredient = ((ISmeltingRecipe) smeltingRecipe.get()).getIngredient();
                            final Optional<ItemStack> remainingItem = ingredient.getRemainingItem(inputSlotItemSnapshot);
                            remainingItem.ifPresent(this.inventory.inputSlot::set);
                        }
                    } else {
                        // Keep on smelting
                        offer(Keys.MAX_COOK_TIME, maxCookTime);
                        offer(Keys.PASSED_COOK_TIME, (int) (elapsedCookTime + elapsed1));
                        break;
                    }
                } else if (elapsedCookTime > 0) {
                    // Undo smelting progress
                    final long time = elapsedCookTime - elapsed1 * 2;
                    offer(Keys.MAX_COOK_TIME, time <= 0 ? 0 : maxCookTime);
                    offer(Keys.PASSED_COOK_TIME, (int) (time <= 0 ? 0 : time));
                    break;
                }
            } else {
                offer(Keys.MAX_COOK_TIME, 0);
                offer(Keys.PASSED_COOK_TIME, 0);
            }
        }

        BlockState blockState = getLocation().getBlock();

        final boolean burning = get(Keys.PASSED_BURN_TIME).get() < get(Keys.MAX_BURN_TIME).get();
        final boolean blockBurning = blockState.getType() == BlockTypes.LIT_FURNACE;

        if (burning != blockBurning) {
            blockState = (burning ? BlockTypes.LIT_FURNACE : BlockTypes.FURNACE).getDefaultState()
                    .withTrait(LanternEnumTraits.HORIZONTAL_FACING, blockState
                            .getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get()).get();
            getLocation().setBlock(blockState, Cause.source(this).build());
        }
    }

    @Override
    public boolean smelt() {
        final ItemStack itemStack = this.inventory.inputSlot.getRawItemStack();
        if (itemStack != null) {
            // Check if the item can be smelted, this means finding a compatible
            // recipe and the output has to be empty.
            final ItemStackSnapshot itemStackSnapshot = itemStack.createSnapshot();
            final Optional<SmeltingRecipe> smeltingRecipe = Lantern.getRegistry().getSmeltingRecipeRegistry()
                    .findMatchingRecipe(itemStackSnapshot);
            final Optional<SmeltingResult> smeltingResult = smeltingRecipe.flatMap(recipe -> recipe.getResult(itemStackSnapshot));
            // Check if the item can be smelted
            if (smeltingResult.isPresent()) {
                final int quantity = ((ISmeltingRecipe) smeltingRecipe.get()).getIngredient().getQuantity(itemStackSnapshot);
                if (itemStack.getQuantity() >= quantity) {
                    final ItemStack result = smeltingResult.get().getResult().createStack();
                    // Check if the result could be added to the output
                    final PeekOfferTransactionsResult peekResult = this.inventory.outputSlot
                            .peekOfferFastTransactions(result);
                    if (peekResult.getOfferResult().isSuccess()) {
                        this.inventory.inputSlot.poll(quantity);
                        this.inventory.outputSlot.offer(result);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public TileEntityInventory<TileEntityCarrier> getInventory() {
        return this.inventory;
    }
}
