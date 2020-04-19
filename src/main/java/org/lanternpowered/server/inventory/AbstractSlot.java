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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.inventory.ItemStack;

@SuppressWarnings("unchecked")
public abstract class AbstractSlot extends AbstractMutableInventory implements ISlot {

    public static final int DEFAULT_MAX_STACK_SIZE = 64;

    /**
     * Constructs a new {@link AbstractInventorySlot.Builder}.
     *
     * @return The builder
     */
    public static AbstractInventorySlot.Builder<AbstractInventorySlot> builder() {
        return AbstractInventorySlot.builder();
    }

    /**
     * Gets the raw {@link LanternItemStack}. Does not make a copy.
     *
     * @return The raw item stack
     */
    public abstract LanternItemStack getRawItemStack();

    /**
     * Sets the raw {@link LanternItemStack}. Does not make a copy.
     *
     * @param itemStack The raw item stack
     */
    public abstract void setRawItemStack(ItemStack itemStack);

    @Override
    public AbstractSlot viewedSlot() {
        return this;
    }

    /**
     * Adds the {@link SlotChangeTracker}.
     *
     * @param tracker The slot change tracker
     */
    public abstract void addTracker(SlotChangeTracker tracker);

    /**
     * Removes the {@link SlotChangeTracker}.
     *
     * @param tracker The slot change tracker
     */
    public abstract void removeTracker(SlotChangeTracker tracker);

    AbstractSlot() {
    }
}
