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

import org.lanternpowered.server.inventory.AbstractInventorySlot;
import org.lanternpowered.server.inventory.SlotChangeTracker;

import java.util.Optional;

public interface ContainerBase extends SlotChangeTracker {

    /**
     * Gets a {@link ClientSlot} for the given slot index
     * within this {@link ContainerPart}.
     *
     * @param index The slot index
     * @return The client slot if present, otherwise {@link Optional#empty()}
     */
    Optional<ClientSlot> getClientSlot(int index);

    /**
     * Attempts to get the bound {@link AbstractInventorySlot} for the given
     * index within this {@link ContainerPart}.
     *
     * @param index The slot index within this container part
     * @return The bound slot if present, otherwise {@link Optional#empty()}
     */
    Optional<AbstractInventorySlot> getSlot(int index);

}
