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
package org.lanternpowered.server.network

import com.google.common.reflect.TypeToken
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.handler.Handler
import java.util.concurrent.ConcurrentHashMap

object NettyThreadOnlyHelper {

    private val map = ConcurrentHashMap<Class<out Handler<out Packet>>, Boolean>()

    fun isHandlerNettyThreadOnly(handlerClass: Class<out Handler<out Packet>>): Boolean {
        return this.map.computeIfAbsent(handlerClass) { isHandlerNettyThreadOnly0(it) }
    }

    private fun isHandlerNettyThreadOnly0(handlerClass: Class<out Handler<out Packet>>): Boolean {
        for (method in handlerClass.methods) {
            if (method.name != "handle" || method.parameterCount != 2 || method.isSynthetic) {
                continue
            }
            val params = method.parameterTypes
            if (params[0] != NetworkContext::class.java) {
                continue
            }
            val messageType = TypeToken.of(handlerClass)
                    .getSupertype(Handler::class.java)
                    .resolveType(Handler::class.java.typeParameters[0])
            if (messageType.rawType != params[1]) {
                continue
            }
            if (method.getAnnotation(NettyThreadOnly::class.java) != null) {
                return true
            }
        }
        return false
    }
}
