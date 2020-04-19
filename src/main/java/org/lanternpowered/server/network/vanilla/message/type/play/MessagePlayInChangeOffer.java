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

public final class MessagePlayInChangeOffer implements Message {

    private final int index;

    /**
     * Creates a new change offer message.
     * 
     * @param index the index
     */
    public MessagePlayInChangeOffer(int index) {
        this.index = index;
    }

    /**
     * Gets the offer index that the sender changed to.
     * 
     * @return the offer index
     */
    public int getIndex() {
        return this.index;
    }

}
