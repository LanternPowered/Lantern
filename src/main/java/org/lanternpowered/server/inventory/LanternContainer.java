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

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.entity.LanternHumanMainInventory;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class LanternContainer extends LanternOrderedInventory implements Container {

    private final Map<Player, ClientContainer> viewers = new HashMap<>();
    final LanternOrderedInventory openInventory;
    final LanternPlayerInventory playerInventory;

    /**
     * The slot for the cursor item.
     */
    private final LanternSlot cursor = new LanternSlot(this);

    /**
     * Creates a new {@link LanternContainer}, the specified {@link PlayerInventory} is
     * used as the bottom inventory and also as top inventory if {@code null} is provided
     * for the inventory that should be opened.
     *
     * @param playerInventory The player inventory
     * @param openInventory The inventory to open
     */
    public LanternContainer(LanternPlayerInventory playerInventory, OpenableInventory openInventory) {
        this(playerInventory, openInventory, openInventory.getName());
    }

    /**
     * Creates a new {@link LanternContainer}, the specified {@link PlayerInventory} is
     * used as the bottom inventory and also as top inventory if {@code null} is provided
     * for the inventory that should be opened.
     *
     * @param playerInventory The player inventory
     * @param openInventory The inventory to open
     * @param name The name of the container
     */
    public LanternContainer(LanternPlayerInventory playerInventory, OpenableInventory openInventory, @Nullable Translation name) {
        this(name, playerInventory, checkNotNull(openInventory, "openInventory"));
    }

    LanternContainer(@Nullable Translation name, LanternPlayerInventory playerInventory, @Nullable OpenableInventory openInventory) {
        super(null, name);
        this.playerInventory = checkNotNull(playerInventory, "playerInventory");
        final LanternHumanMainInventory mainInventory = playerInventory.getMain();
        if (openInventory != null) {
            registerChild(openInventory);
            registerChild(mainInventory);
            this.openInventory = (LanternOrderedInventory) openInventory;
        } else {
            registerChild(playerInventory);
            this.openInventory = playerInventory;
        }
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
    public LanternOrderedInventory getOpenInventory() {
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
    public void open(Player viewer, Cause cause) {
        checkNotNull(viewer, "viewer");
        checkNotNull(cause, "cause");
        ((LanternPlayer) viewer).getContainerSession().setOpenContainer(this, cause);
    }

    @Override
    public void close(Player viewer, Cause cause) {
        checkNotNull(viewer, "viewer");
        checkNotNull(cause, "cause");
        final PlayerContainerSession session = ((LanternPlayer) viewer).getContainerSession();
        if (session.getOpenContainer() == this) {
            session.setOpenContainer(null, cause);
        }
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
    ClientContainer removeViewer(Player viewer) {
        checkNotNull(viewer, "viewer");
        final ClientContainer clientContainer = this.viewers.remove(viewer);
        if (clientContainer != null) {
            removeViewer(viewer, this);
            clientContainer.release();
        }
        return clientContainer;
    }

    /**
     * Adds and opens a {@link ClientContainer} for the {@link Player}.
     *
     * @param viewer The viewer
     * @return The constructed container
     */
    ClientContainer addViewer(Player viewer) {
        checkNotNull(viewer, "viewer");
        checkState(!this.viewers.containsKey(viewer));
        final ClientContainer clientContainer = ((OpenableInventory) this.openInventory).constructClientContainer(this);
        // Bind the default bottom container part if the custom one is missing
        if (!clientContainer.getBottom().isPresent()) {
            final LanternPlayer player = (LanternPlayer) getPlayerInventory().getCarrier().get();
            clientContainer.bindBottom(player.getInventoryContainer().getClientContainer().getBottom().get());
        }
        if (!clientContainer.getInteractionBehavior().isPresent()) {
            final LanternPlayer player = (LanternPlayer) getPlayerInventory().getCarrier().get();
            clientContainer.bindInteractionBehavior(player.getInventoryContainer().getClientContainer().getInteractionBehavior().get());
        }
        this.viewers.put(viewer, clientContainer);
        clientContainer.bind(viewer);
        clientContainer.init();
        addViewer(viewer, this);
        return clientContainer;
    }

    Collection<Player> getRawViewers() {
        return this.viewers.keySet();
    }

    @Override
    public boolean canInteractWith(Player player) {
        return true;
    }
}
