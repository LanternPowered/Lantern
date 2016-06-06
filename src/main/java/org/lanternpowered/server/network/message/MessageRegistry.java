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
package org.lanternpowered.server.network.message;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.message.processor.Processor;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MessageRegistry {

    private final Map<Class<? extends Message>, MessageRegistration<?>> registrationByMessageType = new HashMap<>();
    private final Int2ObjectMap<CodecRegistration<?, ?>> registrationByOpcode = new Int2ObjectOpenHashMap<>();

    <M extends Message> MessageRegistration<M> checkCodecBinding(Class<M> messageType) {
        final MessageRegistration messageRegistration = this.registrationByMessageType.computeIfAbsent(messageType,
                messageType0 -> new MessageRegistration<>(messageType));
        if (messageRegistration.codecRegistration.isPresent()) {
            throw new IllegalArgumentException("The message type " + messageType.getName() +
                    " is already bound to " + ((CodecRegistration) messageRegistration.codecRegistration.get()).getCodec().getClass().getName());
        }
        return messageRegistration;
    }

    /**
     * Registers a new {@link Codec} for the specified opcode.
     *
     * @param opcode the opcode
     * @param codec the codec type
     * @param <M> the type of the processed message
     * @param <C> the type of the codec
     * @return the codec registration
     */
    public <M extends Message, C extends Codec<M>> CodecRegistration<M, C> bind(int opcode, Class<C> codec) {
        try {
            Constructor<C> constructor = codec.getDeclaredConstructor();
            constructor.setAccessible(true);
            return this.bind(opcode, constructor.newInstance());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to instantiate the codec class.", e);
        }
    }

    /**
     * Registers a new {@link Codec} for the specified opcode.
     *
     * @param opcode the opcode
     * @param codec the codec
     * @param messageType the message type
     * @param <M> the type of the processed message
     * @param <C> the type of the codec
     * @return the codec registration
     */
    public <N extends Message, M extends N, C extends Codec<N>> MessageRegistration<M> bind(int opcode, Class<C> codec, Class<M> messageType) {
        MessageRegistration<M> registration = this.checkCodecBinding(messageType);
        CodecRegistration<N, C> codecRegistration = this.bind(opcode, codec);
        codecRegistration.bind(messageType, registration);
        return registration;
    }

    /**
     * Registers a new {@link Codec} for the specified opcode.
     *
     * @param opcode the opcode
     * @param codec the codec
     * @param <M> the type of the processed message
     * @param <C> the type of the codec
     * @return the codec registration
     */
    public <M extends Message, C extends Codec<M>> CodecRegistration<M, C> bind(int opcode, C codec) {
        checkNotNull(codec, "codec");
        if (this.registrationByOpcode.containsKey(opcode)) {
            throw new IllegalArgumentException("Opcode " + opcode + " is already in use by " +
                    this.registrationByOpcode.get(opcode).getCodec().getClass().getName());
        }
        final CodecRegistration<M, C> registration = new CodecRegistration<>(this, opcode, codec);
        this.registrationByOpcode.put(opcode, registration);
        return registration;
    }

    /**
     * Registers a new {@link Codec} for the specified opcode.
     *
     * @param opcode the opcode
     * @param codec the codec
     * @param messageType the message type
     * @param <M> the type of the processed message
     * @param <C> the type of the codec
     * @return the codec registration
     */
    public <N extends Message, M extends N, C extends Codec<N>> MessageRegistration<M> bind(int opcode, C codec, Class<M> messageType) {
        MessageRegistration<M> registration = this.checkCodecBinding(messageType);
        CodecRegistration<N, C> codecRegistration = this.bind(opcode, codec);
        codecRegistration.bind(messageType, registration);
        return registration;
    }

    /**
     * Binds a {@link Message} type to this registry and
     * attaches the {@link Handler} to it.
     *
     * @param messageType the message type
     * @param handler the handler
     * @param <M> the type of the message
     * @param <H> the type of the handler
     * @return the registration
     */
    public <M extends Message, H extends Handler<? super M>> MessageRegistration<M> bindHandler(Class<M> messageType, H handler) {
        MessageRegistration<M> registration = this.bind(messageType);
        registration.bindHandler(handler);
        return registration;
    }

    /**
     * Binds a {@link Message} type to this registry and
     * attaches the {@link Processor} to it.
     *
     * @param messageType the message type
     * @param processor the processor
     * @param <M> the type of the message
     * @param <P> the type of the processor
     * @return the registration
     */
    public <M extends Message, P extends Processor<? super M>> MessageRegistration<M> bindProcessor(Class<M> messageType, P processor) {
        MessageRegistration<M> registration = this.bind(messageType);
        registration.bindProcessor(processor);
        return registration;
    }

    /**
     * Searches a {@link CodecRegistration} for the specified {@link Codec}.
     *
     * @param codec the codec
     * @param <M> the type of the processed message
     * @param <C> the type of the codec
     * @return the codec registration
     */
    public <M extends Message, C extends Codec<M>> Optional<CodecRegistration<M, C>> find(C codec) {
        for (CodecRegistration<?, ?> registration : this.registrationByOpcode.values()) {
            if (codec.equals(registration.getCodec())) {
                return Optional.of((CodecRegistration) registration);
            }
        }
        return Optional.empty();
    }

    /**
     * Searches a {@link CodecRegistration} for the specified {@link Codec} type.
     *
     * @param codec the codec type
     * @param <M> the type of the processed message
     * @param <C> the type of the codec
     * @return the codec registration
     */
    public <M extends Message, C extends Codec<M>> Optional<CodecRegistration<M, C>> find(Class<C> codec) {
        for (CodecRegistration<?, ?> registration : this.registrationByOpcode.values()) {
            if (codec.isInstance(registration.getCodec())) {
                return Optional.of((CodecRegistration) registration);
            }
        }
        return Optional.empty();
    }

    /**
     * Searches a {@link CodecRegistration} for the specified opcode.
     *
     * @param opcode the opcode
     * @param <M> the type of the processed message
     * @param <C> the type of the codec
     * @return the codec registration
     */
    public <M extends Message, C extends Codec<M>> Optional<CodecRegistration<M, C>> find(int opcode) {
        return Optional.ofNullable((CodecRegistration) this.registrationByOpcode.get(opcode));
    }

    /**
     * Searches a {@link MessageRegistration} for the specified message type.
     *
     * @param messageType the message type
     * @param <M> the type of the message
     * @return the message registration
     */
    public <M extends Message> Optional<MessageRegistration<M>> findByMessageType(Class<M> messageType) {
        return Optional.ofNullable((MessageRegistration) this.registrationByMessageType.get(messageType));
    }

    /**
     * Binds a {@link Message} type to this registry.
     *
     * @param messageType the message type
     * @param <M> the type of the message
     * @return the registration
     */
    public <M extends Message> MessageRegistration<M> bind(Class<M> messageType) {
        return (MessageRegistration) this.registrationByMessageType.computeIfAbsent(messageType,
                messageType0 -> new MessageRegistration<>(messageType));
    }

}
