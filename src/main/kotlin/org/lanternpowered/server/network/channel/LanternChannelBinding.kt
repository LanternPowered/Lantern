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
package org.lanternpowered.server.network.channel

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.network.ChannelBinding
import org.spongepowered.api.network.RemoteConnection

internal abstract class LanternChannelBinding(
        private val registrar: LanternChannelRegistrar,
        private val key: CatalogKey
) : ChannelBinding {

    var bound: Boolean = false
    val name = this.key.toString()

    override fun getRegistrar() = this.registrar
    override fun getKey() = this.key

    internal fun checkBound() {
        check(this.bound) { "This channel is no longer bound" }
    }

    /**
     * Handles the message for the remote connection.
     *
     * @param buf The buffer with the content of the message
     * @param connection The connection to decode the packet for
     */
    internal abstract fun handlePayload(buf: ByteBuffer, connection: RemoteConnection)

    /**
     * Handles the login response for the remote connection.
     *
     * @param buf The buffer with the content of the message, if there is one
     * @param connection The connection to decode the packet for
     */
    internal abstract fun handleLoginPayload(buf: ByteBuffer?, transactionId: Int, connection: RemoteConnection)

    override fun toString() = ToStringHelper(this)
            .add("key", this.key)
            .toString()
}
