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
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.channel.MessageReceiver

/**
 * A message channel that targets all [Player]s with the given permission.
 */
object PlayerMessageChannel : MessageChannel {

    override fun getMembers() = Lantern.server.onlinePlayers as Collection<MessageReceiver>
}
