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
package org.lanternpowered.server.text.action

import com.github.benmanes.caffeine.cache.Caffeine
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.text.action.TextActions
import java.util.Optional
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

object LanternClickActionCallbacks {

    /**
     * The base of a [TextActions.executeCallback] command line.
     */
    const val commandBase = "/lantern:textclickcallback "

    /**
     * The pattern of a valid [TextActions.executeCallback] command line.
     */
    val commandPattern = "^$commandBase([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})$".toRegex()

    private val reverseMap = ConcurrentHashMap<UUID, Consumer<CommandCause>>()
    private val callbackCache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .removalListener<Consumer<CommandCause>, UUID> { key, value, _ ->
                this.reverseMap.remove(value, key)
            }
            .build<Consumer<CommandCause>, UUID> { consumer ->
                val uniqueId = UUID.randomUUID()
                this.reverseMap.putIfAbsent(uniqueId, consumer)
                uniqueId
            }

    /**
     * Gets or generates a [UUID] for the specified callback.
     *
     * @param callback The callback
     * @return The unique id
     */
    fun getOrCreateIdForCallback(callback: Consumer<CommandCause>): UUID =
            this.callbackCache[callback] ?: throw IllegalStateException()

    /**
     * Gets the callback for the specified [UUID], may return [Optional.empty]
     * if there is no callback for the specified unique id.
     *
     * @param uniqueId The unique id
     * @return The callback
     */
    fun getCallbackForUUID(uniqueId: UUID): Consumer<CommandCause>? =
            this.reverseMap[uniqueId]
}
