/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

import javax.annotation.Nullable;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.message.processor.Processor;

public interface MessageRegistry {

    <M extends Message, P extends Processor<? super M>> MessageRegistration<M> register(Class<M> message,
            P processor);

    <M extends Message, P extends Processor<? super M>> MessageRegistration<M> register(Class<M> message,
            Class<P> processor);

    <M extends Message, C extends Codec<? super M>> MessageRegistration<M> register(int opcode, Class<M> message,
            Class<C> codec);

    <M extends Message, C extends Codec<? super M>, H extends Handler<? super M>> MessageRegistration<M> register(
            int opcode, Class<M> message, Class<C> codec, @Nullable Class<H> handler);

    <M extends Message, C extends Codec<? super M>, H extends Handler<? super M>> MessageRegistration<M> register(
            int opcode, Class<M> message, Class<C> codec, @Nullable H handler);

    <M extends Message, H extends Handler<? super M>> MessageRegistration<M> register(
            Class<M> message, H handler);

    @Nullable
    <M extends Message> MessageRegistration<M> find(Class<M> message);

    @Nullable
    <M extends Message> MessageRegistration<M> find(int opcode);
}
