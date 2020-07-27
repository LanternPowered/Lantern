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
package org.lanternpowered.server.network.channel

import org.spongepowered.api.ResourceKey
import org.spongepowered.api.network.channel.Channel
import org.spongepowered.api.network.channel.ChannelRegistry
import java.util.Optional

class LanternChannelRegistry : ChannelRegistry {

    override fun getChannels(): Collection<Channel> {
        TODO("Not yet implemented")
    }

    override fun get(channelKey: ResourceKey): Optional<Channel> {
        TODO("Not yet implemented")
    }

    override fun <C : Channel> getOfType(channelKey: ResourceKey, channelType: Class<C>): C {
        TODO("Not yet implemented")
    }
}
