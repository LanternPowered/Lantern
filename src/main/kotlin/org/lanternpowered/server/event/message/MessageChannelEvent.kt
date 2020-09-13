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
package org.lanternpowered.server.event.message

import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.message.MessageCancellable
import org.spongepowered.api.event.message.MessageChannelEvent

fun MessageChannelEvent.sendMessage() {
    if (this is Cancellable && this.isCancelled)
        return
    if (this is MessageCancellable && this.isMessageCancelled)
        return
    this.audience.ifPresent { audience -> audience.sendMessage(this.message) }
}
