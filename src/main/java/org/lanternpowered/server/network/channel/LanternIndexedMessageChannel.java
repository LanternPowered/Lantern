/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.network.channel;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Constructor;
import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nullable;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.Platform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.Message;
import org.spongepowered.api.network.MessageHandler;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.common.collect.Maps;

public class LanternIndexedMessageChannel extends LanternChannelBinding implements ChannelBinding.IndexedMessageChannel {

    private final class RegistrationLookup {

        private final TIntObjectMap<IndexedMessageRegistration> opcodeToRegistration =
                new TIntObjectHashMap<>();
        private final Map<Class<? extends Message>, IndexedMessageRegistration> classToRegistration =
                Maps.newHashMap();

        void add(IndexedMessageRegistration registration) {
            this.opcodeToRegistration.put(registration.opcode, registration);
            this.classToRegistration.put(registration.messageType, registration);
        }
    }

    private final class IndexedMessageRegistration {

        final Class<? extends Message> messageType;
        @Nullable final MessageHandler<? extends Message> handler;
        final int opcode;

        IndexedMessageRegistration(Class<? extends Message> messageType, int opcode,
                @Nullable MessageHandler<? extends Message> handler) {
            this.messageType = messageType;
            this.handler = handler;
            this.opcode = opcode;
        }
    }

    private final EnumMap<Platform.Type, RegistrationLookup> registrations = Maps.newEnumMap(Platform.Type.class);

    LanternIndexedMessageChannel(LanternChannelRegistrar registrar, String name, PluginContainer owner) {
        super(registrar, name, owner);
    }

    private RegistrationLookup getRegistrations(Platform.Type side) {
        RegistrationLookup registrations;
        if (this.registrations.containsKey(side)) {
            registrations = this.registrations.get(side);
        } else {
            this.registrations.put(side, registrations = new RegistrationLookup());
        }
        return registrations;
    }

    @Override
    public void registerMessage(Class<? extends Message> messageClass, int messageId) {
        this.register(messageClass, messageId, null, null);
    }

    @Override
    public <M extends Message> void registerMessage(Class<M> messageClass, int messageId,
            MessageHandler<M> handler) {
        this.register(messageClass, messageId, null, checkNotNull(handler, "handler"));
    }

    @Override
    public <M extends Message> void registerMessage(Class<M> messageClass, int messageId, Platform.Type side,
            MessageHandler<M> handler) {
        this.register(messageClass, messageId, checkNotNull(side, "side"), checkNotNull(handler, "handler"));
    }

    private <M extends Message> void register(Class<M> messageClass, int messageId, Platform.Type side,
            @Nullable MessageHandler<M> handler) {
        checkNotNull(messageClass, "messageClass");
        checkArgument(messageId >= 0 && messageId <= 255, "messageId (" + messageId + ") must scale between 0 and 255");
        if (side == null) {
            this.validate(messageClass, messageId, Platform.Type.CLIENT);
            this.validate(messageClass, messageId, Platform.Type.SERVER);
        } else {
            checkArgument(side.isKnown(), "platform side must be known");
            this.validate(messageClass, messageId, side);
        }
        IndexedMessageRegistration registration = new IndexedMessageRegistration(messageClass, messageId, handler);
        if (side == null) {
            this.getRegistrations(Platform.Type.CLIENT).add(registration);
            this.getRegistrations(Platform.Type.SERVER).add(registration);
        } else {
            this.getRegistrations(side).add(registration);
        }
    }

    private void validate(Class<? extends Message> messageClass, int messageId, Platform.Type side) {
        Constructor<?> constructor = null;
        try {
            constructor = messageClass.getConstructor();
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        checkState(constructor != null, messageClass.getName() + " is missing a empty public contructor.");
        RegistrationLookup registrations = this.getRegistrations(Platform.Type.CLIENT);
        checkState(!registrations.classToRegistration.containsKey(messageClass), "MessageClass (" +
                messageClass.getName() + ") is already registered on the " + side.name().toLowerCase() + " side!");
        checkState(!registrations.opcodeToRegistration.containsKey(messageId), "MessageId (" + messageId +
                ") is already registered on the " + side.name().toLowerCase() + " side! For " +
                registrations.opcodeToRegistration.get(messageId).messageType.getName());
    }

    private void validateRegistration(Class<? extends Message> messageClass, Platform.Type side) {
        checkArgument(this.getRegistrations(side).classToRegistration.containsKey(messageClass),
                messageClass.getName() + " is not registered on the side: " + side.name().toLowerCase());
    }

    private void encode(Message message, ByteBuf buf) {
        LanternChannelBuf content = new LanternChannelBuf(Unpooled.buffer());
        message.writeTo(content);

        buf.writeByte((byte) this.getRegistrations(Platform.Type.SERVER).classToRegistration
                .get(message.getClass()).opcode);
        buf.writeBytes(content.getDelegate());
    }

    @Override
    public void sendTo(Player player, Message message) {
        checkState(this.bound);
        checkNotNull(message, "message");
        this.validateRegistration(message.getClass(), Platform.Type.CLIENT);
        this.registrar.sendPayload(player, this.name, buf -> encode(message, buf));
    }

    @Override
    public void sendToServer(Message message) {
        checkState(this.bound);
        checkNotNull(message, "message");
        this.validateRegistration(message.getClass(), Platform.Type.SERVER);
    }

    @Override
    public void sendToAll(Message message) {
        checkState(this.bound);
        checkNotNull(message, "message");
        this.validateRegistration(message.getClass(), Platform.Type.CLIENT);
        this.registrar.sendPayloadToAll(this.name, buf -> encode(message, buf));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    void handlePayload(ByteBuf buf, RemoteConnection connection) {
        byte opcode = buf.readByte();

        IndexedMessageRegistration registration = this.getRegistrations(Platform.Type.SERVER)
                .opcodeToRegistration.get(opcode);
        if (registration == null) {
            LanternGame.log().warn("Received unexpected message type with id: " + opcode +
                    " in the indexed message channel: " + this.name);
            return;
        }

        Message message;
        try {
            message = registration.messageType.newInstance();
        } catch (Exception e) {
            LanternGame.log().error("Failed to instantiate message: " + registration.messageType.getName(), e);
            return;
        }

        LanternChannelBuf content = new LanternChannelBuf(buf.copy());
        message.readFrom(content);

        if (registration.handler != null) {
            ((MessageHandler) registration.handler).handleMessage(message, connection, Platform.Type.SERVER);
        }
    }
}
