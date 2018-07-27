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
