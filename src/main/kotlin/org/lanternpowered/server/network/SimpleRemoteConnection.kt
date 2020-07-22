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

import io.netty.channel.Channel
import org.spongepowered.api.network.RemoteConnection
import java.net.InetSocketAddress

data class SimpleRemoteConnection(
        private val address: InetSocketAddress,
        private val virtualHostAddress: InetSocketAddress?,
        private val close: () -> Unit
) : RemoteConnection {

    override fun getAddress(): InetSocketAddress = this.address
    override fun getVirtualHost(): InetSocketAddress = this.virtualHostAddress ?: this.address
    override fun close() = this.close.invoke()

    companion object {

        fun of(channel: Channel, virtualHostAddress: InetSocketAddress? = null): SimpleRemoteConnection =
                SimpleRemoteConnection(channel.remoteAddress() as InetSocketAddress, virtualHostAddress) { channel.close() }
    }
}
