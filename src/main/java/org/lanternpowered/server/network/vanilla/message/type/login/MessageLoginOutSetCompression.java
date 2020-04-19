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
package org.lanternpowered.server.network.vanilla.message.type.login;

import org.lanternpowered.server.network.message.Message;

/**
 * The set compression message, this can be send in the
 * LOGIN protocol state.
 */
public final class MessageLoginOutSetCompression implements Message {

    private final int threshold;

    /**
     * Creates a new compression message.
     * 
     * @param threshold the threshold
     */
    public MessageLoginOutSetCompression(int threshold) {
        this.threshold = threshold;
    }

    /**
     * Gets the threshold, it is the max size of a packet before its compressed.
     * Use -1 to disable compression.
     * 
     * @return the threshold
     */
    public int getThreshold() {
        return this.threshold;
    }
}
