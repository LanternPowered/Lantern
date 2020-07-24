package org.lanternpowered.server.event.impl

import org.spongepowered.api.event.Cancellable

abstract class CancellableEvent : Cancellable {

    private var cancelled = false

    override fun setCancelled(cancel: Boolean) { this.cancelled = cancel }
    override fun isCancelled(): Boolean = this.cancelled
}
