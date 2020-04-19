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

import org.lanternpowered.server.inventory.client.ClientSlot;
import org.spongepowered.api.item.inventory.Slot;

/**
 * Can be used to track the changes of a {@link Slot}.
 */
public interface SlotChangeTracker {

    /**
     * Queues a silent slot change for the specified {@link Slot}.
     *
     * @param slot The slot
     */
    void queueSlotChange(Slot slot);

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
     * Queues a silent slot change for the specified {@link Slot}.
     *
     * @param slot The slot
     */
    void queueSilentSlotChange(Slot slot);

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
