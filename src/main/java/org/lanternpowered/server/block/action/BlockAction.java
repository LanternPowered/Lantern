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
package org.lanternpowered.server.block.action;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.chunk.Chunk;

public interface BlockAction {

    /**
     * Fills the {@link BlockActionData} with values that are
     * needed to trigger this action.
     *
     * @param actionData The action data
     */
    void fill(BlockActionData actionData);

    /**
     * Gets the {@link Type}.
     *
     * @return The type
     */
    Type type();

    enum Type {
        /**
         * The event will be present until a new {@link BlockAction}
         * is triggered for the block.
         * <p>The event will only be resend when a {@link Player} starts
         * tracking a {@link Chunk}.</p>
         */
        CONTINUOUS,
        /**
         * The event will be triggered once.
         */
        SINGLE,
    }
}
