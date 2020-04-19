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
     * attaches the {@link Handler} to it.
     *
     * @param messageType The message type
     * @param handler The handler
     * @param <M> The type of the message
     * @param <H> The type of the handler
     * @return The registration
     */
    public <M extends Message, H extends Handler<? super M>> MessageRegistration<M> bindHandler(Class<M> messageType, H handler) {
        final MessageRegistration<M> registration = bindMessage(messageType);
        registration.bindHandler(handler);
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
                //noinspection unchecked
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
                //noinspection unchecked
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
        //noinspection unchecked
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
        //noinspection unchecked
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
        //noinspection unchecked
        return (MessageRegistration) this.registrationByMessageType.computeIfAbsent(messageType,
                messageType0 -> new MessageRegistration<>(messageType));
    }

}
