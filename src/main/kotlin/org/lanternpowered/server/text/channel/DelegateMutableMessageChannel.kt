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
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.text.channel.MessageReceiver
import org.spongepowered.api.text.chat.ChatType
import java.util.Optional


/**
 * A mutable message channel that leaves transforming and
 * members to the delegate channel passed.
 *
 * The members from the provided channel are copied into our
 * own local collection.
 *
 * @param delegate The delegate channel
 */
class DelegateMutableMessageChannel(private val delegate: MessageChannel) : AbstractMutableMessageChannel() {

    init {
        this.members.addAll(this.delegate.members)
    }

    override fun transformMessage(sender: Any?, recipient: MessageReceiver, original: Text, type: ChatType): Optional<Text> =
            this.delegate.transformMessage(sender, recipient, original, type)
}
