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
package org.lanternpowered.server.inventory.block;

import org.lanternpowered.server.event.LanternEventHelper;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.LanternCraftingGridInventory;
import org.lanternpowered.server.inventory.LanternCraftingInventory;
import org.lanternpowered.server.inventory.entity.HumanInventoryView;
import org.lanternpowered.server.inventory.slot.LanternCraftingInput;
import org.lanternpowered.server.inventory.slot.LanternCraftingOutput;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class CraftingTableInventory extends LanternCraftingInventory implements ICraftingTableInventory {

    public CraftingTableInventory(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);

        registerSlot(new LanternCraftingOutput(this));
        registerChild(new LanternCraftingGridInventory(this) {
            {
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        registerSlotAt(x, y, new LanternCraftingInput(this));
                    }
                }
                finalizeContent();
            }
        });
        addCloseListener(inventory -> {
            if (parent instanceof CarriedInventory) {
                ((CarriedInventory<Carrier>) parent).getCarrier().ifPresent(carrier -> {
                    if (carrier instanceof Locatable) {
                        final Cause cause = Cause.source(this).named(NamedCause.owner(carrier)).build();
                        final Location<World> location = ((Locatable) carrier).getLocation();
                        LanternEventHelper.fireDropItemEventDispense(cause, entities -> getCraftingGrid().slots().forEach(
                                slot -> slot.poll().filter(stack -> !stack.isEmpty()).ifPresent(
                                        stack -> entities.add(LanternEventHelper.createDroppedItem(location, stack.createSnapshot())))));
                    }
                });
            }
        });

        finalizeContent();
    }

    @Nullable
    @Override
    protected World getWorld() {
        final AbstractInventory parent = parent();
        if (parent instanceof CarriedInventory) {
            final Optional<Carrier> optCarrier = ((CarriedInventory<Carrier>) parent).getCarrier();
            if (optCarrier.isPresent() && optCarrier.get() instanceof Locatable) {
                return ((Locatable) optCarrier.get()).getWorld();
            }
        }
        return super.getWorld();
    }

    @Override
    public IInventory getShiftClickTarget(LanternContainer container, Slot slot) {
        if (slot instanceof LanternCraftingInput && isChild(slot)) {
            // The crafting input inventories use reverse insertion order to the default
            return container.getPlayerInventory().getInventoryView(HumanInventoryView.PRIORITY_MAIN_AND_HOTBAR);
        }
        // Use the default behavior, you can't shift click to the crafting grid
        return ICraftingTableInventory.super.getShiftClickTarget(container, slot);
    }
}
