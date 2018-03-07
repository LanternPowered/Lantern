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
package org.lanternpowered.server.network;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.handler.MessageHandler;

public final class LanternMessageHandler<M extends Message> implements MessageHandler<M> {

    private final Class<M> messageType;
    private final MessageHandler<? super M> handler;
    private final boolean async;

    LanternMessageHandler(Class<M> messageType, MessageHandler<? super M> handler, boolean async) {
        this.messageType = messageType;
        this.handler = handler;
        this.async = async;
    }

    /**
     * Gets the message {@link Class} that
     * will be handled by this handler.
     *
     * @return The message type
     */
    public Class<M> getMessageType() {
        return this.messageType;
    }

    /**
     * Gets whether this {@link MessageHandler}
     * should be handled async.
     *
     * @return Is async
     */
    public boolean isAsync() {
        return this.async;
    }

    @Override
    public void handle(NetworkContext context, M message) {
        this.handler.handle(context, message);
    }
}
