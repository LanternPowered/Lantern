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

import org.lanternpowered.api.ext.*
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
