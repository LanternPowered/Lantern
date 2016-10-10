/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.entity;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.entity.living.player.Player;

import java.util.function.Supplier;

public interface EntityProtocolUpdateContext {

    static EntityProtocolUpdateContext empty() {
        return EmptyEntityUpdateContext.INSTANCE;
    }

    /**
     * Sends the {@link Message} to the owner, will only do something
     * if the owner is a {@link Player}.
     *
     * @param message The message
     */
    void sendToSelf(Message message);

    /**
     * Sends the {@link Message} to the owner, will only do something
     * if the owner is a {@link Player}.
     *
     * @param messageSupplier The message supplier
     */
    void sendToSelf(Supplier<Message> messageSupplier);

    /**
     * Sends the {@link Message} to all the trackers.
     *
     * @param message The message
     */
    void sendToAll(Message message);

    /**
     * Sends the {@link Message} to all the trackers.
     *
     * @param message The message
     */
    void sendToAll(Supplier<Message> message);

    /**
     * Sends the {@link Message} to all the trackers except the owner.
     *
     * @param message The message
     */
    void sendToAllExceptSelf(Message message);

    /**
     * Sends the {@link Message} to all the trackers except the owner.
     *
     * @param messageSupplier The message supplier
     */
    void sendToAllExceptSelf(Supplier<Message> messageSupplier);
}
