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

/**
 * Represents a viewable inventory which provides it's own {@link AbstractContainer}.
 */
public interface IContainerProvidedInventory extends IViewableInventory {

    /**
     * Constructs a {@link AbstractContainer} for this inventory. This container
     * will be used instead to display it to a viewer {@link Player}.
     *
     * @param viewer The viewer the container is constructed for
     * @return The constructed container
     */
    AbstractContainer createContainer(Player viewer);
}
