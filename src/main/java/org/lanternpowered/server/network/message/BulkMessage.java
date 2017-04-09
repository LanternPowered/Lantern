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
package org.lanternpowered.server.network.message;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.util.collect.Lists2;

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
                .add("messages", Lists2.toString(this.messages))
                .toString();
    }
}
