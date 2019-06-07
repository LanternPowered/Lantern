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
