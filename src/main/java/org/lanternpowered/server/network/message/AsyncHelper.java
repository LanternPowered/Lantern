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

import org.lanternpowered.server.network.message.handler.Handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AsyncHelper {

    private static final Map<Class<?>, Boolean> map = new ConcurrentHashMap<>();

    /**
     * Gets whether the specified handler will be handled asynchronous.
     * 
     * @param handler the handler
     * @return is asynchronous
     */
    public static boolean isAsyncHandler(Handler<?> handler) {
        return isAsync0(handler.getClass());
    }

    /**
     * Gets whether the specified handler will be handled asynchronous.
     * 
     * @param handler the handler
     * @return is asynchronous
     */
    public static boolean isAsyncHandler(Class<? extends Handler<?>> handler) {
        return isAsync0(handler);
    }

    /**
     * Gets whether the specified message will be handled asynchronous.
     * 
     * @param message the message
     * @return is asynchronous
     */
    public static boolean isAsyncMessage(Message message) {
        return isAsync0(message.getClass());
    }

    /**
     * Gets whether the specified message will be handled asynchronous.
     * 
     * @param message the message
     * @return is asynchronous
     */
    public static boolean isAsyncMessage(Class<? extends Message> message) {
        return isAsync0(message);
    }

    private static boolean isAsync0(Class<?> target) {
        if (map.containsKey(target)) {
            return map.get(target);
        }
        boolean async = false;
        while (!async && target != null && target != Object.class) {
            async = target.getAnnotation(Async.class) != null;
            target = target.getSuperclass();
        }
        map.put(target, async);
        return async;
    }

    private AsyncHelper() {
    }
}
