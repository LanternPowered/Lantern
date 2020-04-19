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

import org.lanternpowered.api.util.collections.immutableWeakSetOf
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.channel.MessageReceiver

/**
 * A message channel that targets the given recipients.
 *
 * @param recipients The message receivers
 */
class FixedMessageChannel(recipients: Iterable<MessageReceiver>) : MessageChannel {

    private val recipients: Set<MessageReceiver> = immutableWeakSetOf(recipients)

    override fun getMembers(): Collection<MessageReceiver> = this.recipients
}
