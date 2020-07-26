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
package org.lanternpowered.testserver

import org.lanternpowered.api.Server
import org.lanternpowered.api.event.lifecycle.StartedServerEvent
import org.lanternpowered.api.event.lifecycle.StoppingServerEvent
import org.lanternpowered.api.injector.inject
import org.lanternpowered.api.logger.Logger
import org.lanternpowered.api.text.serializer.LegacyTextSerializer
import org.lanternpowered.api.plugin.Plugin
import org.lanternpowered.api.event.Listener
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * A plugin which broadcasts the server to the LAN. This could
 * save you a few seconds of precious time.
 */
@Plugin("lan-broadcast")
object LANBroadcastPlugin {

    private val logger: Logger = inject()
    private val server: Server = inject()

    private lateinit var socket: DatagramSocket

    @Listener
    fun onServerStarted(event: StartedServerEvent) {
        try {
            this.socket = DatagramSocket()
        } catch (ex: IOException) {
            this.logger.error("Failed to open socket, the LAN broadcast will NOT work.", ex)
            return
        }

        val thread = Thread(this::broadcast, "lan_broadcast")
        thread.isDaemon = true
        thread.start()

        this.logger.info("Started to broadcast the server on the LAN.")
    }

    @Listener
    fun onServerStopping(event: StoppingServerEvent) {
        this.socket.close()
    }

    private fun broadcast() {
        // Formatting codes are still supported by the LAN motd
        val motd = LegacyTextSerializer.serialize(this.server.motd)
        val port = this.server.boundAddress.get().port

        val message = "[MOTD]$motd[/MOTD][AD]$port[/AD]"
        val data = message.toByteArray(Charsets.UTF_8)

        val targetPort = 4445
        val targetAddress = InetAddress.getByName("224.0.2.60")

        var errorLogged = false

        while (!this.socket.isClosed) {
            try {
                this.socket.send(DatagramPacket(data, data.size, targetAddress, targetPort))
                errorLogged = false
            } catch (ex: IOException) {
                if (!errorLogged) {
                    this.logger.error("Failed to broadcast server on the LAN.", ex)
                    errorLogged = true
                }
            }

            // Sleep 1.5 seconds
            Thread.sleep(1500L)
        }
    }
}
