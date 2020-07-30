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
package org.lanternpowered.server.service.permission

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.util.collections.immutableSetBuilderOf
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.Locatable
import org.lanternpowered.server.LanternGame
import org.spongepowered.api.network.RemoteConnection
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.context.ContextCalculator
import org.spongepowered.api.service.permission.Subject
import java.net.InetAddress
import java.util.UUID

/**
 * A context calculator handling world contexts.
 */
class LanternContextCalculator(
        private val game: LanternGame
) : ContextCalculator<Subject> {

    private val ipCache: LoadingCache<RemoteConnection, IpAddressCacheEntry> =
            Caffeine.newBuilder()
                    .weakKeys()
                    .build<RemoteConnection, IpAddressCacheEntry> { connection ->
                        val localIp = buildAddressContexts(Context.LOCAL_IP_KEY, connection.virtualHost.address)
                        val remoteIp = buildAddressContexts(Context.REMOTE_IP_KEY, connection.address.address)
                        IpAddressCacheEntry(localIp, remoteIp)
                    }

    private fun buildAddressContexts(contextKey: String, address: InetAddress): Set<Context> {
        val builder = immutableSetBuilderOf<Context>()
        builder.add(Context(contextKey, address.hostAddress))
        for ((key, sets) in this.game.config.server.ipBasedContexts) {
            if (sets.all { set -> set.test(address) })
                builder.add(Context(contextKey, key))
        }
        return builder.build()
    }

    private class IpAddressCacheEntry(
            val local: Set<Context>,
            val remote: Set<Context>
    )

    private fun Subject.getSourceSubject(): Subject? {
        if (this is UserSubject)
            return game.server.getPlayer(this.uniqueId).orNull()
        val identifier = this.identifier
        if (identifier == game.systemSubject.identifier) {
            return game.systemSubject
        } else {
            val uniqueId = try {
                 UUID.fromString(identifier)
            } catch (ex: Exception) {
                return null
            }
            return game.server.getPlayer(uniqueId).orNull()
        }
    }

    override fun accumulateContexts(subject: Subject, accumulator: MutableSet<Context>) {
        val source = subject.getSourceSubject() ?: return
        if (source is Locatable) {
            val world = source.serverLocation.world
            accumulator.add(world.dimension.type.context)
            accumulator.add(world.context)
        }
        var connection: RemoteConnection? = null
        if (source is Player) {
            connection = source.connection
        }
        // TODO: RCON source
        if (connection != null) {
            val ipEntry = this.ipCache.get(connection)!!
            accumulator.addAll(ipEntry.remote)
            accumulator.addAll(ipEntry.local)
            accumulator.add(Context(Context.LOCAL_PORT_KEY, connection.virtualHost.port.toString()))
            accumulator.add(Context(Context.LOCAL_HOST_KEY, connection.virtualHost.hostName))
        }
    }

    override fun matches(context: Context, subject: Subject): Boolean {
        val source = subject.getSourceSubject() ?: return false
        if (source is Locatable) {
            val world = source.serverLocation.world
            if (context.key == Context.WORLD_KEY)
                return world.context == context
            if (context.key == Context.DIMENSION_KEY)
                return world.dimension.type.context == context
        }
        var connection: RemoteConnection? = null
        if (source is Player) {
            connection = source.connection
        }
        // TODO: RCON source
        if (connection != null) {
            if (context.key == Context.LOCAL_HOST_KEY)
                return connection.virtualHost.hostName == context.value
            if (context.key == Context.LOCAL_PORT_KEY)
                return connection.virtualHost.port.toString() == context.value
            val isLocalIp = context.key == Context.LOCAL_IP_KEY
            if (isLocalIp || context.key == Context.REMOTE_IP_KEY) {
                val ipEntry = this.ipCache.get(connection)!!
                if (isLocalIp) {
                    return ipEntry.local.contains(context)
                } else {
                    return ipEntry.remote.contains(context)
                }
            }
        }
        return false
    }
}
