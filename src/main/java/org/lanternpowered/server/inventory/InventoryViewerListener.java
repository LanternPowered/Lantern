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

import org.spongepowered.api.entity.living.player.Player;

public interface InventoryViewerListener {

    /**
     * Is called when the specified {@link Player} starts watching the {@link AbstractContainer}.
     *
     * @param viewer The viewer
     * @param container The container
     * @param callback The callback
     */
    void onViewerAdded(Player viewer, AbstractContainer container, Callback callback);

    /**
     * Is called when the specified {@link Player} stops watching the {@link AbstractContainer}.
     *
     * @param viewer The viewer
     * @param container The container
     * @param callback The callback
     */
    void onViewerRemoved(Player viewer, AbstractContainer container, Callback callback);

    interface Callback { // TODO: Better name?

        /**
         * Removes the {@link InventoryViewerListener}, the
         * listener won't be run again.
         */
        void remove();
    }
}
