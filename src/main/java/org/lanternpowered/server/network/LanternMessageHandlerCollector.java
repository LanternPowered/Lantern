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
package org.lanternpowered.server.network;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import io.netty.channel.Channel;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.handler.Async;
import org.lanternpowered.server.network.message.handler.ContextInject;
import org.lanternpowered.server.network.message.handler.NetworkMessageHandler;
import org.lanternpowered.server.network.message.handler.HandlerBinder;
import org.lanternpowered.server.network.message.handler.MessageHandler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.util.FieldAccessFactory;
import org.lanternpowered.server.util.LambdaFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
final class LanternMessageHandlerCollector {

    final static class HandlersObjectType {

        final List<UntargetedMethodHandler> handlers = new ArrayList<>();
        final List<BiConsumer<Object, NetworkSession>> sessionSetters = new ArrayList<>();
        final List<BiConsumer<Object, Channel>> channelSetters = new ArrayList<>();
    }

    /**
     * A handler that isn't targeting a specific handler object. This means
     * that this handler can be reused for multiple objects. This is only
     * used for method message handlers.
     */
    final static class UntargetedMethodHandler {

        final Class<?> messageClass;
        final BiConsumer<Object, Message> handler;
        final boolean async;

        UntargetedMethodHandler(Class<?> messageClass,
                BiConsumer<Object, Message> handler, boolean async) {
            this.messageClass = messageClass;
            this.handler = handler;
            this.async = async;
        }
    }

    final static class TargetedMethodHandler<M extends Message> implements MessageHandler<M> {

        final Object target;
        final BiConsumer<Object, Message> handler;

        TargetedMethodHandler(Object target, BiConsumer<Object, Message> handler) {
            this.target = target;
            this.handler = handler;
        }

        @Override
        public void handle(NetworkContext context, M message) {
            this.handler.accept(this.target, message);
        }
    }

    /**
     * Cache all the method message handlers, things will get registered/unregistered
     * quite a lot so avoid regenerating classes/reflection lookups.
     */
    private static final Map<Class<?>, HandlersObjectType> handlersObjectTypeByClass = new ConcurrentHashMap<>();

    /**
     * A map with all the generated {@link UntargetedMethodHandler} for a specific method.
     */
    private static final Map<Method, UntargetedMethodHandler> untargetedMethodHandlerByMethod = new ConcurrentHashMap<>();

    /**
     * Loads {@link LanternMessageHandler}s for the given {@link NetworkSession} and {@link ProtocolState}.
     *
     * @param session The network session
     * @param state The protocol state
     * @param handlers The message handlers
     */
    static void load(NetworkSession session, ProtocolState state, List<LanternMessageHandler> handlers) {
        final HandlerBinder binder = new HandlerBinder() {

            @Override
            public <M extends Message> void bind(Class<M> messageType, MessageHandler<? super M> handler) {
                checkNotNull(messageType, "messageType");
                final boolean async = handler.getClass().getAnnotation(Async.class) != null;
                handlers.add(new LanternMessageHandler<>(messageType, handler, async));
            }

            @Override
            public void bind(Object object) {
                // Get the untargeted method handlers and make them targeted
                final HandlersObjectType handlersObjectType = handlersObjectTypeByClass.computeIfAbsent(
                        object.getClass(), LanternMessageHandlerCollector::loadHandlersObjectType);
                handlersObjectType.handlers.forEach(handler -> handlers.add(new LanternMessageHandler(
                        handler.messageClass, new TargetedMethodHandler(object, handler.handler), handler.async)));
                handlersObjectType.sessionSetters.forEach(consumer -> consumer.accept(object, session));
                handlersObjectType.channelSetters.forEach(consumer -> consumer.accept(object, session.getChannel()));
            }
        };
        state.getProtocol().inbound().getHandlerProviders().forEach(provider -> provider.accept(session, binder));
    }

    private static HandlersObjectType loadHandlersObjectType(Class<?> objectClass) {
        final HandlersObjectType objectType = new HandlersObjectType();
        loadHandlersObjectType(objectType, objectClass);
        return objectType;
    }

    private static void loadHandlersObjectType(
            HandlersObjectType objectType, Class<?> objectClass) {
        for (Method method : objectClass.getDeclaredMethods()) {
            // Only add entries for methods that are declared, to avoid
            // duplicate entries when methods are overridden.
            if (method.getDeclaredAnnotation(NetworkMessageHandler.class) == null) {
                continue;
            }
            checkState(!Modifier.isStatic(method.getModifiers()),
                    "MessageHandler methods cannot be static");
            checkState(method.getReturnType().equals(void.class),
                    "MessageHandler methods cannot have a return type");
            checkState(method.getParameterCount() == 1 && Message.class.isAssignableFrom(method.getParameterTypes()[0]),
                    "MessageHandler methods can only have one parameter and must extend Message");
            final UntargetedMethodHandler methodHandler = untargetedMethodHandlerByMethod
                    .computeIfAbsent(method, method1 -> {
                        final boolean async = method.getDeclaredAnnotation(Async.class) != null;
                        return new UntargetedMethodHandler(method1.getParameterTypes()[0],
                                LambdaFactory.createBiConsumer(method1), async);
                    });
            objectType.handlers.add(methodHandler);
        }
        for (Class<?> interf : objectClass.getInterfaces()) {
            loadHandlersObjectType(objectType, interf);
        }
        for (Field field : objectClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.getAnnotation(ContextInject.class) == null) {
                continue;
            }
            if (field.getType().equals(NetworkSession.class)) {
                objectType.sessionSetters.add(FieldAccessFactory.createSetter(field));
            } else if (field.getType().equals(Channel.class)) {
                objectType.channelSetters.add(FieldAccessFactory.createSetter(field));
            } else {
                throw new IllegalStateException(field.getType() + " cannot be injected through @ContextInject");
            }
        }
        objectClass = objectClass.getSuperclass();
        if (objectClass != null && objectClass != Object.class) {
            loadHandlersObjectType(objectType, objectClass);
        }
    }
}
