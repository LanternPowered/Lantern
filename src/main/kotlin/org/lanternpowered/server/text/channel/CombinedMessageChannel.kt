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

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.api.util.optional.optional
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.channel.MessageReceiver
import org.spongepowered.api.text.chat.ChatType
import java.util.Optional

/**
 * A message channel that targets all subjects contained within the given
 * channels and applies the message transformations of each channel in
 * order (so with n channels,
 * `channels[n-1].transformMessage(channels[n-2]
 * .transformMessage(channels[...]
 * .transformMessage(channels[0].transformMessage(input))))` would occur).
 */
class CombinedMessageChannel(channels: Iterable<MessageChannel>) : MessageChannel {

    private val channels = channels.toImmutableSet()

    override fun transformMessage(sender: Any?, recipient: MessageReceiver, original: Text, type: ChatType): Optional<Text> {
        var text = original
        for (channel in this.channels) {
            text = channel.transformMessage(sender, recipient, text, type).orElse(text)
        }
        return text.optional()
    }

    override fun getMembers(): Collection<MessageReceiver> {
        return this.channels.stream()
                .flatMap { channel -> channel.members.stream() }
                .toImmutableSet()
    }
}
