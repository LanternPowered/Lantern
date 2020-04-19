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

import org.lanternpowered.api.util.collections.weakSetOf
import org.spongepowered.api.text.channel.MessageReceiver
import org.spongepowered.api.text.channel.MutableMessageChannel


/**
 * An abstract implementation of [MutableMessageChannel].
 *
 * The passed [Collection] directly affects the members of this
 * channel.
 *
 * It is recommended to use a weak collection to avoid memory leaks. If
 * you do not use a weak collection, please ensure that members are cleaned
 * up properly.
 *
 * @param receivers The collection of members
 */
abstract class AbstractMutableMessageChannel protected constructor(
        private val receivers: MutableCollection<MessageReceiver> = weakSetOf()
) : MutableMessageChannel {

    override fun addMember(member: MessageReceiver) = this.receivers.add(member)
    override fun removeMember(member: MessageReceiver) = this.receivers.remove(member)
    override fun clearMembers() = run { this.receivers.clear() }
    override fun getMembers() = this.receivers
}
