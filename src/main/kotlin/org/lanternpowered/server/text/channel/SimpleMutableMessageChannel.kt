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
import org.spongepowered.api.text.channel.MutableMessageChannel

/**
 * A simple implementation of [MutableMessageChannel].
 */
class SimpleMutableMessageChannel : AbstractMutableMessageChannel {

    /**
     * Creates a new mutable [MessageChannel].
     */
    constructor()

    /**
     * Creates a new mutable [MessageChannel] with the provided
     * [Collection] of [MessageReceiver]s.
     *
     * @param members The members to add to this channel by default
     */
    constructor(members: MutableCollection<MessageReceiver>) : super(members)
}
