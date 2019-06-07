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
package org.lanternpowered.server.inventory.vanilla;

import static java.util.Objects.requireNonNull;

import org.lanternpowered.server.event.LanternEventHelper;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.InventoryCloseListener;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.inventory.transformation.InventoryTransforms;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This {@link InventoryCloseListener} returns all the contents back to the
 * {@link org.spongepowered.api.entity.living.player.Player} carrier if applicable
 * and enough space, the rest items are dropped on the ground.
 */
@SuppressWarnings("unchecked")
public class PlayerReturnItemsInventoryCloseListener implements InventoryCloseListener {

    @Nullable private final QueryOperation<?>[] dropSelectors;

    /**
     * Constructs a new {@link PlayerReturnItemsInventoryCloseListener}.
     *
     * @param dropSelectors The queries to select the slots/inventories of which the contents should be dropped, the given queries are chained
     */
    public PlayerReturnItemsInventoryCloseListener(QueryOperation<?>... dropSelectors) {
        this.dropSelectors = requireNonNull(dropSelectors, "dropSelectors");
    }

    /**
     * Constructs a new {@link PlayerReturnItemsInventoryCloseListener}.
     */
    public PlayerReturnItemsInventoryCloseListener() {
        this.dropSelectors = null;
    }

    @Override
    public void onClose(IInventory inventory) {
        if (!(inventory instanceof CarriedInventory)) {
            throw new IllegalStateException("Cannot drop items for inventory without carrier.");
        }
        final Optional<Carrier> optCarrier = ((CarriedInventory) inventory).getCarrier();
        if (!optCarrier.isPresent()) {
            throw new IllegalStateException("Cannot drop items for inventory without carrier.");
        }
        final Carrier carrier = optCarrier.get();
        if (!(carrier instanceof Locatable)) {
            throw new IllegalStateException("Cannot drop items for carrier without location.");
        }
        final Location location = ((Locatable) carrier).getLocation();
        if (this.dropSelectors != null) {
            for (QueryOperation<?> dropSelector : this.dropSelectors) {
                inventory = inventory.query(dropSelector);
            }
        }
        final List<ItemStack> items = inventory.slots().stream()
                .map(Slot::poll)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
        if (carrier instanceof Player) {
            // Try to offer the dropped items back to the player inventory
            inventory = (IInventory) ((PlayerInventory) carrier.getInventory()).getPrimary()
                    .transform(InventoryTransforms.PRIORITY_SELECTED_SLOT_AND_HOTBAR);
            // TODO: Event?
            // The following method consumes items that are added to the inventory,
            // the rest is afterwards dropped on the ground
            items.forEach(inventory::offerFast);
        }
        final Transform transform = new Transform<>(location);
        final List<Tuple<ItemStackSnapshot, Transform>> entries = items.stream()
                .filter(item -> !item.isEmpty())
                .map(item -> new Tuple<>((ItemStackSnapshot) LanternItemStackSnapshot.wrap(item), transform))
                .collect(Collectors.toList());
        if (!entries.isEmpty()) {
            LanternEventHelper.handleDroppedItemSpawning(entries, SpongeEventFactory::createDropItemEventClose);
        }
    }
}
