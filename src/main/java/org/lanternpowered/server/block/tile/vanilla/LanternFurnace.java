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

import org.lanternpowered.server.block.tile.IBlockEntityCarrier;
import org.lanternpowered.server.block.tile.LanternBlockEntity;
import org.lanternpowered.server.block.trait.LanternBooleanTraits;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.element.ElementListener;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.PeekedOfferTransactionResult;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.block.FurnaceInventory;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.lanternpowered.server.item.recipe.fuel.IFuel;
import org.lanternpowered.server.item.recipe.smelting.ISmeltingRecipe;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.carrier.Furnace;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.type.TileEntityInventory;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;
import org.spongepowered.api.util.Direction;

import java.util.Optional;
import java.util.OptionalInt;

public class LanternFurnace extends LanternBlockEntity implements Furnace, IBlockEntityCarrier {

    // The inventory of the furnace
    private final FurnaceInventory inventory;

    // The tick since the last pulse
    private long lastTick = -1;

    public LanternFurnace() {
        this.inventory = VanillaInventoryArchetypes.FURNACE.builder()
                .withCarrier(this).build(Lantern.getMinecraftPlugin());
        this.inventory.enableCachedProgress();
    }

    @Override
    public void registerKeys() {
        super.registerKeys();

        final ElementListener<Integer> clearProperty = (oldElement, newElement) -> this.inventory.resetCachedProgress();

        final ValueCollection c = getValueCollection();
        c.register(Keys.DISPLAY_NAME, null);
        c.register(Keys.MAX_BURN_TIME, 0, 0, Integer.MAX_VALUE).addListener(clearProperty);
        c.register(Keys.PASSED_BURN_TIME, 0, 0, Keys.MAX_BURN_TIME).addListener(clearProperty);
        c.register(Keys.MAX_COOK_TIME, 0, 0, Integer.MAX_VALUE).addListener(clearProperty);
        c.register(Keys.PASSED_COOK_TIME, 0, 0, Keys.MAX_COOK_TIME).addListener(clearProperty);
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

            Optional<SmeltingResult> smeltingResult = Optional.empty();
            Optional<SmeltingRecipe> smeltingRecipe = Optional.empty();
            LanternItemStack itemStack = this.inventory.getInputSlot().getRawItemStack();
            final ItemStackSnapshot inputSlotItemSnapshot = itemStack.createSnapshot();
            if (!inputSlotItemSnapshot.isEmpty()) {
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
                            final PeekedOfferTransactionResult peekResult = this.inventory.getOutputSlot().peekOffer(
                                    smeltingResult.get().getResult().createStack());
                            if (!peekResult.isEmpty()) {
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

            int elapsedCookTime = require(Keys.PASSED_COOK_TIME);
            int remainingCookTime = maxCookTime - elapsedCookTime;

            if (maxCookTime > 0 && elapsed1 > remainingCookTime) {
                elapsed1 = remainingCookTime;
            }
            elapsed -= elapsed1;

            // Burn items until the furnace is burning properly
            int maxBurnTime = require(Keys.MAX_BURN_TIME);
            int elapsedBurnTime = require(Keys.PASSED_BURN_TIME);
            int remainingBurnTime = maxBurnTime - elapsedBurnTime;

            long elapsed2 = elapsed1;
            while (elapsed2 >= remainingBurnTime) {
                elapsed2 -= remainingBurnTime;
                // Reset the max burn time
                maxBurnTime = 0;
                // Only burn a new item if the target item can be smelted
                itemStack = this.inventory.getFuelSlot().getRawItemStack();
                if (itemStack.isFilled() && maxCookTime > 0) {
                    // Check for the next fuel item
                    final ItemStackSnapshot itemStackSnapshot = itemStack.createSnapshot();
                    final Optional<IFuel> result = Lantern.getRegistry().getFuelRegistry().findMatching(itemStackSnapshot);
                    if (result.isPresent()) {
                        final OptionalInt optBurnTime = result.get().getBurnTime(itemStackSnapshot);
                        // We have a next matching burn item, check if we can poll one and then continue burning
                        if (optBurnTime.isPresent() && this.inventory.getFuelSlot().poll(1).isFilled()) {
                            maxBurnTime = optBurnTime.getAsInt();
                            remainingBurnTime = maxBurnTime;
                            elapsedBurnTime = 0;
                            // Put the rest item in the slot, if the slot is empty
                            if (this.inventory.getFuelSlot().size() == 0) {
                                final IIngredient ingredient = result.get().getIngredient();
                                final Optional<ItemStack> remainingItem = ingredient.getRemainingItem(itemStackSnapshot);
                                remainingItem.ifPresent(this.inventory.getFuelSlot()::setForced);
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
                        this.inventory.getOutputSlot().offer(smeltingResult.get().getResult().createStack());
                        this.inventory.getInputSlot().poll(quantity);

                        // Put the rest item in the slot
                        if (this.inventory.getInputSlot().size() == 0) {
                            final IIngredient ingredient = ((ISmeltingRecipe) smeltingRecipe.get()).getIngredient();
                            final Optional<ItemStack> remainingItem = ingredient.getRemainingItem(inputSlotItemSnapshot);
                            remainingItem.ifPresent(this.inventory.getInputSlot()::set);
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

        final boolean burning = require(Keys.PASSED_BURN_TIME) < require(Keys.MAX_BURN_TIME);
        final boolean blockBurning = blockState.getTraitValue(LanternBooleanTraits.LIT).get();

        if (burning != blockBurning) {
            getLocation().setBlock(blockState.withTrait(LanternBooleanTraits.LIT, burning).get());
        }
    }

    @Override
    public boolean smelt() {
        final LanternItemStack itemStack = this.inventory.getInputSlot().getRawItemStack();
        if (itemStack.isFilled()) {
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
                    final PeekedOfferTransactionResult peekResult = this.inventory.getOutputSlot().peekOffer(result);
                    if (!peekResult.isEmpty()) {
                        this.inventory.getInputSlot().poll(quantity);
                        this.inventory.getOutputSlot().offer(result);
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

    @Override
    public Inventory getInventory(Direction from) {
        switch (from) {
            case EAST:
            case WEST:
            case SOUTH:
            case NORTH:
                return this.inventory.getFuelSlot();
            case UP:
                return this.inventory.getInputSlot();
            case DOWN:
                // TODO: Limited access to the fuel slot to pull out empty buckets?
                return this.inventory.getOutputSlot();
            default:
                return IBlockEntityCarrier.super.getInventory(from);
        }
    }
}
