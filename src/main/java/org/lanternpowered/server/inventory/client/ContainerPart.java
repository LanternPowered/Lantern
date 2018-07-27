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
