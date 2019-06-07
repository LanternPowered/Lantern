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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class CodecRegistration<M extends Message, C extends Codec<? super M>> {

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
