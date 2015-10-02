package org.lanternpowered.server.network.message;

import java.util.Map;

import org.lanternpowered.server.network.message.handler.Handler;

import com.google.common.collect.Maps;

public final class AsyncHelper {

    private static final Map<Class<?>, Boolean> map = Maps.newConcurrentMap();

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
