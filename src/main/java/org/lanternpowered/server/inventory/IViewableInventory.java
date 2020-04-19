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

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.Set;

@SuppressWarnings("ConstantConditions")
public interface IViewableInventory extends IInventory, ViewableInventory {

    // The viewers map will never be null if this interface is implemented

    @Override
    default Set<Player> getViewers() {
        return ImmutableSet.copyOf(((AbstractInventory) this).getViewersMap().keySet());
    }

    @Override
    default boolean hasViewers() {
        return !((AbstractInventory) this).getViewersMap().isEmpty();
    }

    /**
     * Gets whether the specified player can interact with this inventory.
     *
     * <p>Defaults to {@code true} unless overridden.</p>
     *
     * @param player The player that wants to interacts with this inventory
     * @return Whether the player can interact with this inventory
     */
    @Override
    default boolean canInteractWith(Player player) {
        return true;
    }
}
