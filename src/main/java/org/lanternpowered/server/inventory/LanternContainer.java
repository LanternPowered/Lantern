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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.behavior.VanillaContainerInteractionBehavior;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ClientContainerType;
import org.lanternpowered.server.inventory.type.slot.LanternSlot;
import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.GuiId;
import org.spongepowered.api.item.inventory.property.GuiIdProperty;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public class LanternContainer extends AbstractOrderedInventory implements Container {

    /**
     * Creates a new {@link LanternContainer}, the specified {@link LanternPlayerInventory} is
     * used as the bottom inventory and also as top inventory if {@code null} is provided
     * for the inventory that should be opened.
     *
     * @param playerInventory The player inventory
     * @param openInventory The inventory to open
     */
    public static LanternContainer construct(LanternPlayerInventory playerInventory, AbstractOrderedInventory openInventory) {
        if (openInventory instanceof CarriedInventory) {
            return new CarriedLanternContainer<>(playerInventory, openInventory);
        }
        return new LanternContainer(playerInventory, openInventory);
    }

    private final Map<Player, ClientContainer> viewers = new HashMap<>();

    final AbstractOrderedInventory openInventory;
    final LanternPlayerInventory playerInventory;

    /**
     * The slot for the cursor item.
     */
    private final LanternSlot cursor = new LanternSlot();

    @SuppressWarnings("unchecked")
    LanternContainer(LanternPlayerInventory playerInventory, AbstractOrderedInventory openInventory) {
        this.playerInventory = playerInventory;
        this.openInventory = openInventory;
        final List<AbstractOrderedInventory> inventories = ImmutableList.of(openInventory, playerInventory.getMain());
        final List<AbstractContainerSlot> slots = new ArrayList<>();
        for (AbstractOrderedInventory inventory : inventories) {
            for (AbstractSlot slot : inventory.getIndexedSlotInventories()) {
                final AbstractContainerSlot containerSlot = ((AbstractInventorySlot) slot).constructContainerSlot();
                containerSlot.slot = (AbstractInventorySlot) slot;
                containerSlot.setParent(this);
                slots.add(containerSlot);
            }
        }
        initWithSlots((List) inventories, slots, null);
    }

    @Override
    public EmptyInventory empty() {
        return super.empty();
    }

    /**
     * Gets the cursor {@link LanternSlot}.
     *
     * @return The cursor slot
     */
    public LanternSlot getCursorSlot() {
        return this.cursor;
    }

    /**
     * Gets the {@link Inventory} that is being opened. It is possible that this
     * inventory is equal to {@link #getPlayerInventory()} in case the player
     * opened it's own inventory.
     *
     * @return The inventory
     */
    public AbstractOrderedInventory getOpenInventory() {
        return this.openInventory;
    }

    /**
     * Gets the {@link LanternPlayerInventory}.
     *
     * @return The player inventory
     */
    public LanternPlayerInventory getPlayerInventory() {
        return this.playerInventory;
    }

    @Override
    public Set<Player> getViewers() {
        return ImmutableSet.copyOf(this.viewers.keySet());
    }

    @Override
    public boolean hasViewers() {
        return !this.viewers.isEmpty();
    }

    @Override
    public void open(Player viewer) {
        checkNotNull(viewer, "viewer");
        ((LanternPlayer) viewer).getContainerSession().setOpenContainer(this);
    }

    @Override
    public void close(Player viewer) {
        checkNotNull(viewer, "viewer");
        final PlayerContainerSession session = ((LanternPlayer) viewer).getContainerSession();
        if (session.getOpenContainer() == this) {
            session.setOpenContainer(null);
        }
    }

    @Override
    void queryInventories(Set<AbstractMutableInventory> inventories, Predicate<AbstractMutableInventory> predicate) {
        super.queryInventories(inventories, inventory -> !(inventory instanceof AbstractSlot) && predicate.test(inventory));
        getSlotInventories().stream().filter(predicate::test).forEach(inventories::add);
    }

    /**
     * Attempts to get the {@link ClientContainer} for the
     * specified {@link Player}. {@link Optional#empty()} will
     * be returned if the {@link Player} isn't watching this
     * container.
     *
     * @param viewer The viewer
     * @return The client container
     */
    public Optional<ClientContainer> getClientContainer(Player viewer) {
        checkNotNull(viewer, "viewer");
        return Optional.ofNullable(this.viewers.get(viewer));
    }

    /**
     * Tries to get a {@link ClientContainer}.
     *
     * @param viewer The viewer
     * @return The client container
     */
    public ClientContainer tryGetClientContainer(Player viewer) {
        return getClientContainer(viewer).orElseThrow(
                () -> new IllegalStateException("Unable to find a client container for a viewer: " + viewer.getName()));
    }

    @Nullable
    void removeViewer(Player viewer) {
        checkNotNull(viewer, "viewer");
        final ClientContainer clientContainer = this.viewers.remove(viewer);
        if (clientContainer != null) {
            removeViewer(viewer, this);
            clientContainer.release();
        }
    }

    /**
     * Adds and opens a {@link ClientContainer} for the {@link Player}.
     *
     * @param viewer The viewer
     */
    void addViewer(Player viewer) {
        checkNotNull(viewer, "viewer");
        checkState(!this.viewers.containsKey(viewer));
        final ClientContainer clientContainer;
        // Get the gui id (ClientContainerType)
        final GuiId guiId = this.openInventory.getInventoryProperty(GuiIdProperty.class)
                .map(GuiIdProperty::getValue).orElseThrow(IllegalStateException::new);
        clientContainer = ((ClientContainerType) guiId).createContainer(this.openInventory);
        clientContainer.bindCursor(this.cursor);
        clientContainer.bindInteractionBehavior(new VanillaContainerInteractionBehavior(this));
        this.openInventory.initClientContainer(clientContainer);
        // Bind the default bottom container part if the custom one is missing
        if (!clientContainer.getBottom().isPresent()) {
            final LanternPlayer player = (LanternPlayer) getPlayerInventory().getCarrier().get();
            clientContainer.bindBottom(player.getInventoryContainer().getClientContainer().getBottom().get());
        }
        this.viewers.put(viewer, clientContainer);
        clientContainer.bind(viewer);
        clientContainer.init();
        addViewer(viewer, this);
    }

    Collection<Player> getRawViewers() {
        return this.viewers.keySet();
    }

    @Override
    public boolean canInteractWith(Player player) {
        return true;
    }
}
