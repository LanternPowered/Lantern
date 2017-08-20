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
package org.lanternpowered.server.inventory.slot;

import org.lanternpowered.server.inventory.client.ClientSlot;

public interface SlotChangeTracker {

    /**
     * Queues a silent slot change for the specified {@link LanternSlot}.
     *
     * @param slot The slot
     */
    void queueSlotChange(LanternSlot slot);

    /**
     * Queues a slot change for the specified {@link ClientSlot}.
     *
     * @param clientSlot The client slot
     */
    void queueSlotChange(ClientSlot clientSlot);

    /**
     * Queues a slot change for the specified slot index.
     *
     * @param index The slot index
     */
    void queueSlotChange(int index);

    /**
     * Queues a silent slot change for the specified {@link LanternSlot}.
     *
     * @param slot The slot
     */
    void queueSilentSlotChange(LanternSlot slot);

    /**
     * Queues a silent slot change for the specified {@link ClientSlot}.
     *
     * @param clientSlot The client slot
     */
    void queueSilentSlotChange(ClientSlot clientSlot);

    /**
     * Queues a silent slot change for the specified slot index.
     *
     * @param index The slot index
     */
    void queueSilentSlotChange(int index);
}
