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
package org.lanternpowered.server.network

import com.google.common.reflect.TypeToken
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.message.handler.Handler
import java.util.concurrent.ConcurrentHashMap

object NettyThreadOnlyHelper {

    private val map = ConcurrentHashMap<Class<out Handler<out Message>>, Boolean>()

    fun isHandlerNettyThreadOnly(handlerClass: Class<out Handler<out Message>>): Boolean {
        return this.map.computeIfAbsent(handlerClass) { isHandlerNettyThreadOnly0(it) }
    }

    private fun isHandlerNettyThreadOnly0(handlerClass: Class<out Handler<out Message>>): Boolean {
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
