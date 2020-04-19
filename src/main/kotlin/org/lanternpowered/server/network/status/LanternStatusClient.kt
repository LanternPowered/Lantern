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
package org.lanternpowered.server.network.status

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.ToStringHelper
import org.spongepowered.api.MinecraftVersion
import org.spongepowered.api.network.status.StatusClient
import java.net.InetSocketAddress

class LanternStatusClient(
        private val address: InetSocketAddress,
        private val version: MinecraftVersion,
        private val virtualHost: InetSocketAddress?
) : StatusClient {

    override fun getAddress() = this.address
    override fun getVersion() = this.version
    override fun getVirtualHost() = this.virtualHost.optional()

    override fun toString(): String {
        return ToStringHelper(this)
                .omitNullValues()
                .add("address", this.address)
                .add("virtualHost", this.virtualHost)
                .add("version", this.version)
                .toString()
    }
}
