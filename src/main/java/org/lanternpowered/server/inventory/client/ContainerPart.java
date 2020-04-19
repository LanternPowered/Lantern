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
package org.lanternpowered.server.inventory.client;

import org.lanternpowered.server.inventory.AbstractSlot;
import org.spongepowered.api.item.inventory.ItemStack;

public interface ContainerPart extends ContainerBase {

    /**
     * Gets the root {@link ClientContainer}.
     *
     * @return The root client container
     */
    ClientContainer getRoot();

    /**
     * Unbinds/releases the slot of the given index
     * within this {@link ContainerPart}.
     *
     * @param index The index within this container part
     */
    void unbind(int index);

    /**
     * Binds a {@link AbstractSlot} to the given slot index
     * within this {@link ContainerPart}.
     *
     * @param index The slot index within this container part
     * @return The bound client slot
     */
    ClientSlot.Slot bindSlot(int index, AbstractSlot slot);

    /**
     * Binds a {@link ItemStack} as a icon to the
     * given slot index within this {@link ContainerPart}.
     *
     * @param index The slot index within this container part
     * @return The bound client slot
     */
    ClientSlot.Button bindButton(int index);

    /**
     * Gets the index for the {@link ClientSlot} within
     * this {@link ContainerPart}. The returned index will be
     * {@code -1} if the slot isn't located in this part.
     *
     * @param clientSlot The client slot
     * @return The slot index within this container part
     */
    int getSlotIndex(ClientSlot clientSlot);
}
