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

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.processor.Processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class MessageRegistration<M extends Message> {

    private final Class<M> messageType;
    Optional<CodecRegistration<? super M, Codec<? super M>>> codecRegistration = Optional.empty();
    private List<Processor<? super M>> processors = new ArrayList<>();
    private List<Processor<? super M>> unmodifiableProcessors = Collections.unmodifiableList(this.processors);

    MessageRegistration(Class<M> messageType) {
        this.messageType = messageType;
    }

    /**
     * Gets the message type of this registration.
     *
     * @return The message type
     */
    public Class<M> getMessageType() {
        return this.messageType;
    }

    /**
     * Gets the {@link CodecRegistration} that is bound to this message registration,
     * may be {@link Optional#empty()}.
     *
     * @return The codec registration, if present
     */
    public Optional<CodecRegistration<? super M, Codec<? super M>>> getCodecRegistration() {
        return this.codecRegistration;
    }

    /**
     * Gets the {@link Processor}s that are bound to this message registration.
     *
     * @return The processors
     */
    public List<Processor<? super M>> getProcessors() {
        return this.unmodifiableProcessors;
    }

    /**
     * Binds the processor to this message registration.
     *
     * @param processor The processor
     * @return This message registration, for chaining
     */
    public MessageRegistration<M> bindProcessor(Processor<? super M> processor) {
        this.processors.add(processor);
        return this;
    }

    /**
     * Binds the processor to this message registration.
     *
     * @param index The index to insert the processor at
     * @param processor The processor
     * @return This message registration, for chaining
     */
    public MessageRegistration<M> bindProcessor(int index, Processor<? super M> processor) {
        if (index >= this.processors.size()) {
            this.processors.add(processor);
        } else {
            this.processors.add(index < 0 ? 0 : index, processor);
        }
        return this;
    }
}
