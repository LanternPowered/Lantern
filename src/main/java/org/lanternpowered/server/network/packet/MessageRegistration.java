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
package org.lanternpowered.server.network.packet;

import org.lanternpowered.server.network.packet.codec.Codec;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.packet.processor.Processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class MessageRegistration<M extends Packet> {

    private final Class<M> messageType;
    Optional<CodecRegistration<? super M, Codec<? super M>>> codecRegistration = Optional.empty();
    private Optional<Handler<? super M>> handler = Optional.empty();
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
     * Gets the {@link Handler} that is bound to this message registration,
     * may be {@link Optional#empty()}.
     *
     * @return The handler, if present
     */
    public Optional<Handler<? super M>> getHandler() {
        return this.handler;
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
     * Binds the handler to this message registration.
     *
     * @param handler The handler
     * @return This message registration, for chaining
     */
    public MessageRegistration<M> bindHandler(@Nullable Handler<? super M> handler) {
        this.handler = Optional.ofNullable(handler);
        return this;
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
