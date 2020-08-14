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
package org.lanternpowered.server.text

import com.github.benmanes.caffeine.cache.Caffeine
import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.server.util.UUIDHelper
import org.spongepowered.api.adventure.SpongeComponents
import org.spongepowered.api.command.CommandCause
import java.util.Optional
import java.util.UUID
import java.util.function.Consumer
import kotlin.time.Duration
import kotlin.time.minutes
import kotlin.time.toJavaDuration

object ClickActionCallbacks {

    /**
     * The duration after which a duration callback will expire.
     */
    val expireDuration: Duration = 10.minutes

    /**
     * The base of a [SpongeComponents.executeCallback] command line.
     */
    private const val commandBase = "/lantern:textclickcallback"

    /**
     * The pattern of a valid [SpongeComponents.executeCallback] command line.
     */
    private val commandRegex = "^$commandBase (${UUIDHelper.regex.pattern})$".toRegex()

    private val reverse = concurrentHashMapOf<UUID, Consumer<CommandCause>>()
    private val callbacks = Caffeine.newBuilder()
            .expireAfterAccess(this.expireDuration.toJavaDuration())
            .removalListener<Consumer<CommandCause>, UUID> { key, value, _ ->
                this.reverse.remove(value, key)
            }
            .build<Consumer<CommandCause>, UUID> { consumer ->
                val uniqueId = UUID.randomUUID()
                this.reverse.putIfAbsent(uniqueId, consumer)
                uniqueId
            }

    /**
     * Attempts to parse the given [command] as a callback. Returns `null` if parsing
     * failed, returns [Optional] if it was successful. The consumer can still be `empty`
     * if the consumer entry expired.
     */
    fun parseCallbackCommand(command: String): Optional<Consumer<CommandCause>>? {
        val match = this.commandRegex.find(command) ?: return null
        val uniqueId = UUID.fromString(match.groupValues[1])
        return Optional.ofNullable(this.reverse[uniqueId])
    }

    /**
     * Gets or generates a command for the specified callback.
     *
     * @param callback The callback
     * @return The command
     */
    fun getOrCreateCallbackCommand(callback: Consumer<CommandCause>): String {
        val uniqueId = this.callbacks[callback]!!
        return "$commandBase $uniqueId"
    }
}
