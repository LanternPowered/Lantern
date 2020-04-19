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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInDropHeldItem implements Message {

    private final boolean fullStack;

    public MessagePlayInDropHeldItem(boolean fullStack) {
        this.fullStack = fullStack;
    }

    /**
     * Whether a full item stack should be dropped
     * instead of one item.
     *
     * @return is full stack
     */
    public boolean isFullStack() {
        return this.fullStack;
    }
}
