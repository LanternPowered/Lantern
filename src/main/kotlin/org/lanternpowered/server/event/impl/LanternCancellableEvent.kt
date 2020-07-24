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

import org.spongepowered.api.event.Cancellable

abstract class CancellableEvent : Cancellable {

    private var cancelled = false

    override fun setCancelled(cancel: Boolean) { this.cancelled = cancel }
    override fun isCancelled(): Boolean = this.cancelled
}
