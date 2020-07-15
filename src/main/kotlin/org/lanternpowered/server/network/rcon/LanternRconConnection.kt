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
package org.lanternpowered.server.network.rcon

import io.netty.channel.Channel
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.permission.AbstractProxySubject
import org.spongepowered.api.network.RconConnection
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.util.Tristate
import java.net.InetSocketAddress

class LanternRconConnection internal constructor(
        private val channel: Channel,
        private val virtualHost: InetSocketAddress
) : AbstractProxySubject(), RconConnection {

    private val buffer = StringBuffer()
    private val identifier: String = "Rcon[${address.hostName}]"
    @Volatile private var authorized = false

    init {
        initializeSubject()
    }

    override fun getAddress(): InetSocketAddress = this.channel.remoteAddress() as InetSocketAddress
    override fun getVirtualHost(): InetSocketAddress = this.virtualHost
    override fun getIdentifier(): String = this.identifier
    override fun isAuthorized(): Boolean = this.authorized
    override fun setAuthorized(authorized: Boolean) { this.authorized = authorized }

    override fun close() {
        // Flush remaining content first
        this.flush()
        this.channel.close()
    }

    override fun sendMessage(message: Text) {
        this.buffer.append(message.toPlain()).append('\n')
    }

    override fun getMessageChannel(): MessageChannel = MessageChannel.toPlayersAndServer()
    override fun setMessageChannel(channel: MessageChannel) {}

    override val subjectCollectionIdentifier: String
        get() = PermissionService.SUBJECTS_SYSTEM

    override fun getPermissionDefault(permission: String): Tristate = Tristate.TRUE

    fun flush(): String {
        val result = this.buffer.toString()
        this.buffer.setLength(0)
        return result
    }

    override fun toString(): String = ToStringHelper(this)
            .add("address", this.address)
            .add("virtualHost", this.virtualHost)
            .toString()
}
