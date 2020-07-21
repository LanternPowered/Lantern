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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class CodecRegistration<M extends Packet, C extends Codec<? super M>> {

    // The message types that are bound to this codec
    private final Set<Class<? extends M>> boundMessageTypes = new HashSet<>();
    // The message types that are bound to this codec
    private final Set<Class<? extends M>> unmodifiableBoundMessageTypes = Collections.unmodifiableSet(this.boundMessageTypes);
    // The registry this registration is bound to
    private final MessageRegistry registry;

    private final int opcode;
    private final C codec;

    public CodecRegistration(MessageRegistry registry, int opcode, C codec) {
        this.registry = registry;
        this.opcode = opcode;
        this.codec = codec;
    }

    public Set<Class<? extends M>> getBoundMessageTypes() {
        return this.unmodifiableBoundMessageTypes;
    }

    /**
     * Binds the message type to this codec registration.
     *
     * @param messageType the message type
     * @throws IllegalArgumentException if the message type is
     *         already bound to another codec registration
     */
    public <A extends M> MessageRegistration<A> bind(Class<A> messageType) throws IllegalArgumentException {
        return this.bind(messageType, null);
    }

    <A extends M> MessageRegistration<A> bind(Class<A> messageType, @Nullable MessageRegistration<A> registration)
            throws IllegalArgumentException {
        if (registration == null) {
            registration = this.registry.checkCodecBinding(messageType);
        }
        this.boundMessageTypes.add(messageType);
        registration.codecRegistration = Optional.of((CodecRegistration) this);
        return registration;
    }

    /**
     * Gets the opcode of the registration.
     *
     * @return the opcode
     */
    public int getOpcode() {
        return this.opcode;
    }

    /**
     * Gets the codec.
     *
     * @return the codec
     */
    public C getCodec() {
        return this.codec;
    }

}
