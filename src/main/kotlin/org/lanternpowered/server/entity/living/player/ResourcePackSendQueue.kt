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
package org.lanternpowered.server.entity.living.player

import org.lanternpowered.server.network.vanilla.message.type.play.SetResourcePackMessage
import org.spongepowered.api.event.entity.living.player.ResourcePackStatusEvent.ResourcePackStatus
import org.spongepowered.api.resourcepack.ResourcePack

class ResourcePackSendQueue internal constructor(private val player: LanternPlayer) {

    private val queue = mutableListOf<ResourcePack>()
    private var waitingForResponse: ResourcePack? = null
    private var counter = 0

    fun offer(resourcePack: ResourcePack) {
        synchronized(this.queue) {
            if (this.waitingForResponse == null) {
                send(resourcePack)
                this.counter = 0
            } else {
                this.queue.add(resourcePack)
            }
        }
    }

    fun poll(status: ResourcePackStatus): ResourcePack? {
        synchronized(this.queue) {
            val resourcePack = this.waitingForResponse
            // Just return the status, we will still expect a next
            // status message for this resource pack
            if (!status.wasSuccessful().isPresent) {
                this.counter = -1
                return resourcePack
            }
            if (this.queue.isNotEmpty()) {
                send(this.queue.removeAt(0))
                this.counter = 0
            } else {
                this.waitingForResponse = null
            }
            return resourcePack
        }
    }

    fun pulse() {
        synchronized(this.queue) {
            val waitingForResponse = this.waitingForResponse
            if (waitingForResponse == null || this.counter == -1)
                return
            this.counter++
            this.counter %= RESEND_DELAY
            if (this.counter == 0)
                send(waitingForResponse)
        }
    }

    private fun send(resourcePack: ResourcePack) {
        this.waitingForResponse = resourcePack
        val hash = resourcePack.hash.orElse(resourcePack.id)
        val location = resourcePack.uri.toString()
        this.player.connection.send(SetResourcePackMessage(location, hash))
    }

    companion object {
        // The delay before we resend the resource pack packet,
        // resending until the players clicks yes or no, closing
        // the window isn't an option.
        private const val RESEND_DELAY = 10
    }
}
