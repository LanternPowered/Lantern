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
package org.lanternpowered.server.network.message;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.List;

/**
 * Represents a message that holds a bunch of other messages, this can be
 * used to return multiple messages from a codec.
 */
public final class BulkMessage implements Message {

    public static final BulkMessage EMPTY = new BulkMessage(ImmutableList.of());

    private final List<Message> messages;

    public BulkMessage(List<Message> messages) {
        this.messages = ImmutableList.copyOf(messages);
    }

    /**
     * Gets the messages.
     *
     * @return the messages
     */
    public List<Message> getMessages() {
        return this.messages;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("messages", Iterables.toString(this.messages))
                .toString();
    }
}
