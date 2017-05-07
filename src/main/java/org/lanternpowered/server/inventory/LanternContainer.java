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

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.entity.HumanInventoryView;
import org.lanternpowered.server.inventory.entity.HumanMainInventory;
import org.lanternpowered.server.inventory.entity.LanternPlayerInventory;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowItems;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public abstract class LanternContainer extends LanternOrderedInventory implements Container {

    private static int windowIdCounter = 1;

    final Set<Player> viewers = new HashSet<>();
    private final Map<LanternSlot, Boolean> dirtySlots = new HashMap<>();

    protected final int windowId;

    protected final LanternOrderedInventory openInventory;
    final LanternPlayerInventory playerInventory;

    /**
     * Creates a new {@link LanternContainer}, the specified {@link PlayerInventory} is
     * used as the bottom inventory and also as top inventory if {@code null} is provided
     * for the inventory that should be opened.
     *
     * @param name The name of the container
     * @param playerInventory The player inventory
     * @param openInventory The inventory to open
     */
    public LanternContainer(@Nullable Translation name,
            LanternPlayerInventory playerInventory, @Nullable OrderedInventory openInventory) {
        super(null, name);
        this.playerInventory = playerInventory;
        final HumanMainInventory mainInventory = playerInventory.getMain();
        if (openInventory != null) {
            this.registerChild(openInventory);
            this.registerChild(mainInventory);
            this.windowId = windowIdCounter++;
            if (windowIdCounter >= 100) {
                windowIdCounter = 1;
            }
            this.openInventory = (LanternOrderedInventory) openInventory;
        } else {
            this.registerChild(playerInventory);
            this.openInventory = playerInventory;
            this.windowId = 0;
        }
    }

    void addSlotTrackers() {
        for (LanternSlot slot : this.slots) {
            slot.addContainer(this);
        }
    }

    void removeSlotTrackers() {
        for (LanternSlot slot : this.slots) {
            slot.removeContainer(this);
        }
    }

    /**
     * Gets the {@link Inventory} that is being opened.
     *
     * @return The inventory
     */
    public LanternOrderedInventory getOpenInventory() {
        return this.openInventory;
    }

    @Override
    public Set<Player> getViewers() {
        return ImmutableSet.copyOf(this.viewers);
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

    protected abstract void openInventoryFor(LanternPlayer viewer);

    public void openInventoryForAndInitialize(Player viewer) {
        this.openInventoryFor((LanternPlayer) viewer);

        final List<ItemStack> items = this.slots.stream()
                .map(slot -> slot.peek().orElse(null)).collect(Collectors.toList());
        // Send the inventory content
        ((LanternPlayer) viewer).getConnection().send(
                new MessagePlayOutWindowItems(this.windowId, items.toArray(new ItemStack[items.size()])));
    }

    /**
     * Queues a {@link ItemStack} change of a {@link Slot}.
     *
     * @param slot The slot
     */
    public void queueSlotChange(Slot slot) {
        this.queueSlotChange(slot, false);
    }

    /**
     * Queues a {@link ItemStack} change of a {@link Slot}. This
     * is done "silently" and this means that there won't be any
     * animation played (in the hotbar).
     *
     * @param slot The slot
     */
    public void queueSilentSlotChange(Slot slot) {
        this.queueSlotChange(slot, true);
    }

    void queueSlotChange(Slot slot, boolean silent) {
        if (!this.viewers.isEmpty()) {
            this.queueSlotChange0(slot, silent);
        }
    }

    void queueSlotChange0(Slot slot, boolean silent) {
        int index = this.getSlotIndex(slot);
        if (index != -1) {
            this.dirtySlots.put((LanternSlot) slot, silent);
        }
    }

    Set<Player> getRawViewers() {
        return this.viewers;
    }

    public void streamSlotChanges() {
        final List<Message> messages = new ArrayList<>();
        for (Map.Entry<LanternSlot, Boolean> entry : this.dirtySlots.entrySet()) {
            final LanternSlot slot = entry.getKey();
            int windowId = this.windowId;
            int index = -1;
            if (entry.getValue() && slot.parent() instanceof Hotbar) {
                index = ((LanternOrderedInventory) this.playerInventory.getInventoryView(HumanInventoryView.RAW_INVENTORY)).getSlotIndex(slot);
            }
            if (index == -1) {
                index = this.openInventory.getSlotIndex(slot);
            } else {
                windowId = -2;
            }
            if (index != -1) {
                messages.add(new MessagePlayOutSetWindowSlot(windowId, index, slot.peek().orElse(null)));
            }
        }
        this.dirtySlots.clear();
        if (!messages.isEmpty()) {
            getRawViewers().forEach(player -> ((LanternPlayer) player).getConnection().send(messages));
        }
    }

    @Override
    public boolean canInteractWith(Player player) {
        return true;
    }

}
