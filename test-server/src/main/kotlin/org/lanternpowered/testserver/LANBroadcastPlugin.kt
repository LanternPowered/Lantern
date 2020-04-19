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

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.text.serializer.TextSerializers
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.event.game.state.GameStoppingServerEvent
import org.spongepowered.api.plugin.Plugin
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * A plugin which broadcasts the server to the LAN. This could
 * save you a few seconds of precious time.
 */
@Plugin(
        id = "lan_broadcast",
        name = "LAN Broadcast",
        version = "1.0.0",
        description = "Broadcasts the server to the LAN.",
        authors = [ "Cybermaxke" ]
)
class LANBroadcastPlugin {

    private val logger: Logger by inject()

    private lateinit var socket: DatagramSocket

    @Listener
    fun onServerStarted(event: GameStartedServerEvent) {
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
    fun onServerStopping(event: GameStoppingServerEvent) {
        this.socket.close()
    }

    private fun broadcast() {
        val server = Sponge.getServer()
        // Formatting codes are still supported by the LAN motd
        val motd = TextSerializers.LEGACY_FORMATTING_CODE.serialize(server.motd)
        val port = server.boundAddress.get().port

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
