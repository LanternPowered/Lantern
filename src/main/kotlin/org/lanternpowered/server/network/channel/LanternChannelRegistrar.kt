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

import org.lanternpowered.api.ext.*
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.Platform
import org.spongepowered.api.network.ChannelBinding
import org.spongepowered.api.network.ChannelRegistrar
import org.spongepowered.api.network.packet.PacketChannel
import org.spongepowered.api.network.raw.RawDataChannel
import org.spongepowered.api.network.raw.login.RawLoginDataChannel

class LanternChannelRegistrar : ChannelRegistrar {

    private val channels = mutableMapOf<String, ChannelBinding>()

    override fun <C : ChannelBinding> createChannel(channelKey: CatalogKey, channelType: Class<C>): C {
        val name = channelKey.toString()
        check(name !in this.channels) { "There's already a channel bound with the key $channelKey" }
        val channel: ChannelBinding = when (channelType) {
            RawDataChannel::class.java -> LanternRawDataChannel(this, channelKey)
            RawLoginDataChannel::class.java -> LanternRawLoginDataChannel(this, channelKey)
            PacketChannel::class.java -> LanternPacketChannel(this, channelKey)
            else -> throw UnsupportedOperationException("Unsupported channel type: ${channelType.name}")
        }
        this.channels[name] = channel
        return channelType.cast(channel)
    }

    override fun getChannel(channelKey: CatalogKey) = this.channels[channelKey.toString()].optional()

    override fun <C : ChannelBinding> getOrCreate(channelKey: CatalogKey, channelType: Class<C>): C {
        val name = channelKey.toString()
        val channel = this.channels[name]
        if (channel != null) {
            if (channelType.isInstance(channel)) {
                return channelType.cast(channel)
            }
            throw IllegalStateException("There's already a channel registered for ${channelKey.name} with " +
                    "a different type ${channel.javaClass.asSubclass(ChannelBinding::class.java)}")
        }
        return createChannel(channelKey, channelType)
    }

    override fun unbindChannel(channel: ChannelBinding) {
        channel as LanternChannelBinding
        channel.bound = false
        this.channels.remove(channel.name)
    }

    override fun getRegisteredChannels(side: Platform.Type): Set<CatalogKey>
            = this.channels.keys.stream().map(CatalogKey::resolve).toImmutableSet()

    override fun isChannelAvailable(channelKey: CatalogKey): Boolean {
        if (channelKey.namespace == CatalogKey.MINECRAFT_NAMESPACE) {
            return false
        }
        return channelKey.toString() !in this.channels
    }
}
