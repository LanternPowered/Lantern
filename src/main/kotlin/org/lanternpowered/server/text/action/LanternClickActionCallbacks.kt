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
package org.lanternpowered.server.text.action

import com.github.benmanes.caffeine.cache.Caffeine
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.text.action.TextActions
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
     * Gets the callback for the specified [UUID], may return `null`
     * if there is no callback for the specified unique id.
     *
     * @param uniqueId The unique id
     * @return The callback
     */
    fun getCallbackForUUID(uniqueId: UUID): Consumer<CommandCause>? =
            this.reverseMap[uniqueId]
}
