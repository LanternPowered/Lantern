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
package org.lanternpowered.server.text.channel

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.util.collections.toImmutableSet
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.channel.MessageReceiver

/**
 * A message channel that targets all [Player]s with the given permission.
 *
 * @param permission The permission node
 */
class PlayerPermissionMessageChannel(private val permission: String) : MessageChannel {

    override fun getMembers(): Collection<MessageReceiver> {
        return Lantern.server.onlinePlayers.stream()
                .filter { player: Player -> player.hasPermission(this.permission) }
                .toImmutableSet()
    }
}
