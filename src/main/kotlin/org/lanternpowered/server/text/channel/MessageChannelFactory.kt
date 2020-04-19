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

import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.channel.MessageReceiver

object MessageChannelFactory : MessageChannel.Factory {

    override fun toSubjectsWithPermission(permission: String) = SubjectPermissionMessageChannel(permission)

    override fun combined(vararg channels: MessageChannel) = CombinedMessageChannel(channels.asList())
    override fun combined(channels: Iterable<MessageChannel>) = CombinedMessageChannel(channels)

    override fun to(vararg recipients: MessageReceiver) = FixedMessageChannel(recipients.asList())
    override fun to(recipients: Iterable<MessageReceiver>) = FixedMessageChannel(recipients)

    override fun toNone() = EmptyMessageChannel

    override fun toPlayers() = PlayerMessageChannel
    override fun toPlayersWithPermission(permission: String) = PlayerPermissionMessageChannel(permission)

    override fun toPlayersAndServer(): MessageChannel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toServer(): MessageChannel {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun asMutable(channel: MessageChannel) = DelegateMutableMessageChannel(channel)
}
