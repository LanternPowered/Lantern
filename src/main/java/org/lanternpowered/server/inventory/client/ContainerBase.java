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
import org.lanternpowered.server.inventory.SlotChangeTracker;

import java.util.Optional;

public interface ContainerBase extends SlotChangeTracker {

    /**
     * Gets a {@link ClientSlot} for the given slot index
     * within this {@link ContainerBase}.
     *
     * @param index The slot index
     * @return The client slot if present, otherwise {@link Optional#empty()}
     */
    Optional<ClientSlot> getClientSlot(int index);

    /**
     * Attempts to get the bound {@link AbstractSlot} for the given
     * index within this {@link ContainerBase}.
     *
     * @param index The slot index within this container part
     * @return The bound slot if present, otherwise {@link Optional#empty()}
     */
    Optional<AbstractSlot> getSlot(int index);

}
