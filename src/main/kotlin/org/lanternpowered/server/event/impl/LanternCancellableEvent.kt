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
package org.lanternpowered.server.event.impl

import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.event.Cancellable
import org.lanternpowered.api.event.Event

abstract class CancellableEvent(private val cause: Cause) : Event, Cancellable {

    override fun getCause(): Cause = this.cause

    private var cancelled = false

    override fun setCancelled(cancel: Boolean) { this.cancelled = cancel }
    override fun isCancelled(): Boolean = this.cancelled
}
