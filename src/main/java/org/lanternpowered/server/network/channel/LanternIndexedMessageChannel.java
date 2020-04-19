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
package org.lanternpowered.server.network.channel;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.UncheckedThrowables.throwUnchecked;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.spongepowered.api.Platform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.Message;
import org.spongepowered.api.network.MessageHandler;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.PluginContainer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

final class LanternIndexedMessageChannel extends LanternChannelBinding implements ChannelBinding.IndexedMessageChannel {

    private final class RegistrationLookup {

        private final Byte2ObjectMap<IndexedMessageRegistration> opcodeToRegistration = new Byte2ObjectOpenHashMap<>();
        private final Map<Class<? extends Message>, IndexedMessageRegistration> classToRegistration = new HashMap<>();
    }

    private final class IndexedMessageRegistration {

        final Class<? extends Message> messageType;
        final List<MessageHandler<? extends Message>> handlers = new ArrayList<>();
        @Nullable Byte opcode;

        IndexedMessageRegistration(Class<? extends Message> messageType) {
            this.messageType = messageType;
        }
    }

    private final EnumMap<Platform.Type, RegistrationLookup> registrations = new EnumMap<>(Platform.Type.class);

    LanternIndexedMessageChannel(LanternChannelRegistrar registrar, String name, PluginContainer owner) {
        super(registrar, name, owner);
    }

    private RegistrationLookup getRegistrations(Platform.Type side) {
        final RegistrationLookup registrations;
        if (this.registrations.containsKey(side)) {
            registrations = this.registrations.get(side);
        } else {
            this.registrations.put(side, registrations = new RegistrationLookup());
        }
        return registrations;
    }

    @Override
    public void registerMessage(Class<? extends Message> messageClass, int messageId) {
        register(messageClass, messageId, null, null);
    }

    @Override
    public <M extends Message> void registerMessage(Class<M> messageClass, int messageId, MessageHandler<M> handler) {
        register(messageClass, messageId, null, checkNotNull(handler, "handler"));
    }

    @Override
    public <M extends Message> void registerMessage(Class<M> messageClass, int messageId, Platform.Type side, MessageHandler<M> handler) {
        register(messageClass, messageId, checkNotNull(side, "side"), checkNotNull(handler, "handler"));
    }

    @Override
    public <M extends Message> void addHandler(Class<M> messageClass, Platform.Type side, MessageHandler<M> handler) {
        applyHandler(getRegistrations(checkNotNull(side, "side")), messageClass, checkNotNull(handler, "handler"));
    }

    @Override
    public <M extends Message> void addHandler(Class<M> messageClass, MessageHandler<M> handler) {
        checkNotNull(handler, "handler");
        applyHandler(getRegistrations(Platform.Type.CLIENT), messageClass, handler);
        applyHandler(getRegistrations(Platform.Type.SERVER), messageClass, handler);
    }

    private <M extends Message> void applyHandler(RegistrationLookup lookup, Class<M> messageClass, MessageHandler<M> handler) {
        final IndexedMessageRegistration registration = lookup.classToRegistration.computeIfAbsent(messageClass, IndexedMessageRegistration::new);
        registration.handlers.add(handler);
    }

    private <M extends Message> void register(Class<M> messageClass, int messageId, @Nullable Platform.Type side,
            @Nullable MessageHandler<M> handler) {
        checkNotNull(messageClass, "messageClass");
        checkArgument(messageId >= 0 && messageId <= 255, "MessageId (%s) must scale between 0 and 255", messageId);
        final byte messageId0 = (byte) messageId;
        if (side == null) {
            validate(messageClass, messageId0, Platform.Type.CLIENT);
            validate(messageClass, messageId0, Platform.Type.SERVER);
        } else {
            checkArgument(side.isKnown(), "Platform side must be known");
            validate(messageClass, messageId0, side);
        }
        final IndexedMessageRegistration registration = new IndexedMessageRegistration(messageClass);
        registration.opcode = messageId0;
        if (handler != null) {
            registration.handlers.add(handler);
        }
        if (side == null) {
            applyRegistration(getRegistrations(Platform.Type.CLIENT), messageClass, messageId0, handler);
            applyRegistration(getRegistrations(Platform.Type.SERVER), messageClass, messageId0, handler);
        } else {
            applyRegistration(getRegistrations(side), messageClass, messageId0, handler);
        }
    }

    private <M extends Message> void applyRegistration(RegistrationLookup lookup, Class<M> messageClass, byte messageId,
            @Nullable MessageHandler<M> handler) {
        final IndexedMessageRegistration registration = lookup.classToRegistration.computeIfAbsent(messageClass, IndexedMessageRegistration::new);
        lookup.opcodeToRegistration.put(messageId, registration);
        registration.opcode = messageId;
        if (handler != null) {
            registration.handlers.add(handler);
        }
    }

    private void validate(Class<? extends Message> messageClass, byte messageId, Platform.Type side) {
        Constructor<?> constructor = null;
        try {
            constructor = messageClass.getConstructor();
        } catch (NoSuchMethodException ignored) {
        } catch (SecurityException e) {
            throw throwUnchecked(e);
        }
        checkState(constructor != null, "%s is missing a empty public constructor", messageClass.getName());
        final RegistrationLookup registrations = getRegistrations(Platform.Type.CLIENT);
        checkState(!registrations.classToRegistration.containsKey(messageClass) ||
                registrations.classToRegistration.get(messageClass).opcode == null,
                "MessageClass (%s) is already registered on the %s side!",
                messageClass.getName(), side.name().toLowerCase());
        checkState(!registrations.opcodeToRegistration.containsKey(messageId),
                "MessageId (%s) is already registered on the %s side! For %s",
                messageId, side.name().toLowerCase(), registrations.opcodeToRegistration.get(messageId).messageType.getName());
    }

    private void validateRegistration(Class<? extends Message> messageClass, Platform.Type side) {
        final IndexedMessageRegistration registration = getRegistrations(side).classToRegistration.get(messageClass);
        checkArgument(registration != null && registration.opcode != null,
                "%s is not registered on the side: %s", messageClass.getName(), side.name().toLowerCase());
    }

    private void encode(Message message, ByteBuffer buf) {
        final IndexedMessageRegistration registration = getRegistrations(Platform.Type.SERVER).classToRegistration.get(message.getClass());
        checkArgument(registration != null, "The specified message type %s is not registered", message.getClass().getName());

        final ByteBuffer content = ByteBufferAllocator.unpooled().buffer();
        message.writeTo(content);
        //noinspection ConstantConditions
        buf.writeByte(registration.opcode);
        buf.writeBytes(content);
    }

    @Override
    public void sendTo(Player player, Message message) {
        checkState(this.bound);
        checkNotNull(message, "message");
        validateRegistration(message.getClass(), Platform.Type.CLIENT);
        getRegistrar().sendPayload(player, getName(), buf -> encode(message, buf));
    }

    @Override
    public void sendToServer(Message message) {
        checkState(this.bound);
        checkNotNull(message, "message");
        validateRegistration(message.getClass(), Platform.Type.SERVER);
    }

    @Override
    public void sendToAll(Message message) {
        checkState(this.bound);
        checkNotNull(message, "message");
        validateRegistration(message.getClass(), Platform.Type.CLIENT);
        getRegistrar().sendPayloadToAll(getName(), buf -> encode(message, buf));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    void handlePayload(ByteBuffer buf, RemoteConnection connection) {
        final byte opcode = buf.readByte();

        final IndexedMessageRegistration registration = getRegistrations(Platform.Type.SERVER)
                .opcodeToRegistration.get(opcode);
        if (registration == null) {
            Lantern.getLogger().warn("Received unexpected message type with id: {}" +
                    " in the indexed message channel: {}", opcode, getName());
            return;
        }

        final Message message;
        try {
            message = registration.messageType.newInstance();
        } catch (Exception e) {
            Lantern.getLogger().error("Failed to instantiate message: {}", registration.messageType.getName(), e);
            return;
        }

        final ByteBuffer content = buf.slice();
        try {
            message.readFrom(content);
        } catch (Exception e) {
            Lantern.getLogger().error("Failed to deserialize message: {}", registration.messageType.getName(), e);
            return;
        }

        registration.handlers.forEach(handler -> ((MessageHandler) handler).handleMessage(message, connection, Platform.Type.SERVER));
    }
}
