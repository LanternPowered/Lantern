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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.handler.HandlerBinder;
import org.lanternpowered.server.network.message.handler.MessageHandler;
import org.lanternpowered.server.network.message.processor.Processor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("unchecked")
public final class MessageRegistry {

    private final Map<Class<? extends Message>, MessageRegistration<?>> registrationByMessageType = new HashMap<>();
    private final Int2ObjectMap<CodecRegistration<?, ?>> registrationByOpcode = new Int2ObjectOpenHashMap<>();
    private final List<BiConsumer<NetworkSession, HandlerBinder>> handlerProviders = new ArrayList<>();

    private int opcodeCounter;

    <M extends Message> MessageRegistration<M> checkCodecBinding(Class<M> messageType) {
        final MessageRegistration messageRegistration = this.registrationByMessageType.computeIfAbsent(messageType,
                messageType0 -> new MessageRegistration<>(messageType));
        if (messageRegistration.codecRegistration.isPresent()) {
            throw new IllegalArgumentException("The message type " + messageType.getName() +
                    " is already bound to " + ((CodecRegistration) messageRegistration.codecRegistration.get()).getCodec().getClass().getName());
        }
        //noinspection unchecked
        return messageRegistration;
    }

    public void bind() {
        this.opcodeCounter++;
    }

    public void bind(int count) {
        this.opcodeCounter += count;
    }

    /**
     * Gets a {@link List} with all the handler provider {@link BiConsumer} functions.
     *
     * @return The handler providers
     */
    public List<BiConsumer<NetworkSession, HandlerBinder>> getHandlerProviders() {
        return Collections.unmodifiableList(this.handlerProviders);
    }

    /**
     * Registers a handler provider {@link BiConsumer}. This consumer will be
     * called when {@link MessageHandler}s are being collected for a specific
     * {@link NetworkSession}.
     *
     * @param handlerProvider The handler provider
     */
    public void addHandlerProvider(BiConsumer<NetworkSession, HandlerBinder> handlerProvider) {
        checkNotNull(handlerProvider, "handlerProvider");
        this.handlerProviders.add(handlerProvider);
    }

    /**
     * Registers a new {@link Codec} for the next available opcode.
     *
     * @param codec The codec type
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration
     */
    public <M extends Message, C extends Codec<M>> CodecRegistration<M, C> bind(Class<C> codec) {
        try {
            final Constructor<C> constructor = codec.getDeclaredConstructor();
            constructor.setAccessible(true);
            return bindInstance(this.opcodeCounter++, constructor.newInstance());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to instantiate the codec class.", e);
        }
    }

    /**
     * Registers a new {@link Codec} for the next available opcode.
     *
     * @param codec The codec type
     * @param <M> The type of the processed message
     */
    @SafeVarargs
    public final <M extends Message, C extends Codec<M>> void bind(Class<C> codec, Class<? extends M>... messageTypes) {
        final List<MessageRegistration<? extends M>> registrations = new ArrayList<>();
        for (Class<? extends M> messageType : messageTypes) {
            registrations.add(checkCodecBinding(messageType));
        }
        final CodecRegistration<M, C> codecRegistration = bind(codec);
        registrations.forEach(codecRegistration::bind);
    }

    /**
     * Registers a new {@link Codec} for the specified opcode.
     *
     * @param codec The codec type
     * @param opcode The opcode
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration
     */
    public <M extends Message, C extends Codec<M>> CodecRegistration<M, C> bind(int opcode, Class<C> codec) {
        try {
            final Constructor<C> constructor = codec.getDeclaredConstructor();
            constructor.setAccessible(true);
            return bindInstance(opcode, constructor.newInstance());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to instantiate the codec class.", e);
        }
    }

    /**
     * Registers a new {@link Codec} for the specified opcode.
     *
     * @param codec The codec
     * @param messageType The message type
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration
     */
    public <N extends Message, M extends N, C extends Codec<N>> MessageRegistration<M> bind(Class<C> codec, Class<M> messageType) {
        final MessageRegistration<M> registration = checkCodecBinding(messageType);
        final CodecRegistration<N, C> codecRegistration = bind(codec);
        codecRegistration.bind(messageType, registration);
        return registration;
    }

    /**
     * Registers a new {@link Codec} for the next available opcode.
     *
     * @param codec The codec
     * @param messageType The message type
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration
     */
    public <N extends Message, M extends N, C extends Codec<N>> MessageRegistration<M> bind(int opcode, Class<C> codec, Class<M> messageType) {
        final MessageRegistration<M> registration = checkCodecBinding(messageType);
        final CodecRegistration<N, C> codecRegistration = bind(opcode, codec);
        codecRegistration.bind(messageType, registration);
        return registration;
    }

    /**
     * Registers a new {@link Codec} for the next available opcode.
     *
     * @param codec The codec type
     * @param <M> The type of the processed message
     */
    @SafeVarargs
    public final <C extends Codec<M>, M extends Message> void bind(int opcode, Class<C> codec, Class<? extends M>... messageTypes) {
        final List<MessageRegistration<? extends M>> registrations = new ArrayList<>();
        for (Class<? extends M> messageType : messageTypes) {
            registrations.add(checkCodecBinding(messageType));
        }
        final CodecRegistration<M, C> codecRegistration = bind(opcode, codec);
        registrations.forEach(codecRegistration::bind);
    }

    /**
     * Registers a new {@link Codec} for the specified opcode.
     *
     * @param codec The codec
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration
     */
    public <M extends Message, C extends Codec<M>> CodecRegistration<M, C> bindInstance(C codec) {
        checkNotNull(codec, "codec");
        return bindInstance(this.opcodeCounter++, codec);
    }

    /**
     * Registers a new {@link Codec} for the specified opcode.
     *
     * @param codec The codec
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration
     */
    public <M extends Message, C extends Codec<M>> CodecRegistration<M, C> bindInstance(int opcode, C codec) {
        checkNotNull(codec, "codec");
        final CodecRegistration<M, C> registration = new CodecRegistration<>(this, opcode, codec);
        this.registrationByOpcode.put(opcode, registration);
        return registration;
    }

    /**
     * Registers a new {@link Codec} for the specified opcode.
     *
     * @param codec The codec
     * @param messageType The message type
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration
     */
    public <N extends Message, M extends N, C extends Codec<N>> MessageRegistration<M> bindInstance(C codec, Class<M> messageType) {
        final MessageRegistration<M> registration = checkCodecBinding(messageType);
        final CodecRegistration<N, C> codecRegistration = bindInstance(codec);
        codecRegistration.bind(messageType, registration);
        return registration;
    }

    /**
     * Binds a {@link Message} type to this registry and
     * attaches the {@link Processor} to it.
     *
     * @param messageType The message type
     * @param processor The processor
     * @param <M> The type of the message
     * @param <P> The type of the processor
     * @return The registration
     */
    public <M extends Message, P extends Processor<? super M>> MessageRegistration<M> bindProcessor(Class<M> messageType, P processor) {
        final MessageRegistration<M> registration = bindMessage(messageType);
        registration.bindProcessor(processor);
        return registration;
    }

    /**
     * Searches a {@link CodecRegistration} for the specified {@link Codec}.
     *
     * @param codec The codec
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration
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
     * @param codec The codec type
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration
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
     * @param opcode The opcode
     * @param <M> The type of the processed message
     * @param <C> The type of the codec
     * @return The codec registration, if present
     */
    public <M extends Message, C extends Codec<M>> Optional<CodecRegistration<M, C>> find(int opcode) {
        return Optional.ofNullable((CodecRegistration) this.registrationByOpcode.get(opcode));
    }

    /**
     * Searches a {@link MessageRegistration} for the specified message type.
     *
     * @param messageType the message type
     * @param <M> the type of the message
     * @return The message registration
     */
    public <M extends Message> Optional<MessageRegistration<M>> findByMessageType(Class<M> messageType) {
        return Optional.ofNullable((MessageRegistration) this.registrationByMessageType.get(messageType));
    }

    /**
     * Binds a {@link Message} type to this registry.
     *
     * @param messageType The message type
     * @param <M> The type of the message
     * @return The message registration
     */
    public <M extends Message> MessageRegistration<M> bindMessage(Class<M> messageType) {
        return (MessageRegistration) this.registrationByMessageType.computeIfAbsent(messageType,
                messageType0 -> new MessageRegistration<>(messageType));
    }

}
