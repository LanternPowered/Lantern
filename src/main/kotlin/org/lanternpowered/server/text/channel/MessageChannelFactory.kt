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
package org.lanternpowered.server.text.channel

import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.channel.MessageReceiver
import org.spongepowered.api.world.World

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
