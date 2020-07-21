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
package org.lanternpowered.server.network.vanilla.packet.handler.status

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.text.serializer.JsonTextSerializer
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.game.version.LanternMinecraftVersion
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.WrappedRemoteConnection
import org.lanternpowered.server.network.packet.handler.Handler
import org.lanternpowered.server.network.status.LanternFavicon
import org.lanternpowered.server.network.status.LanternStatusClient
import org.lanternpowered.server.network.status.LanternStatusHelper
import org.lanternpowered.server.network.status.LanternStatusResponse
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusRequestPacket
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusResponsePacket

class StatusRequestHandler : Handler<StatusRequestPacket> {

    private val gson = Gson()

    override fun handle(context: NetworkContext, packet: StatusRequestPacket) {
        val session = context.session
        val server = session.server
        val description = server.motd
        val address = session.address
        val virtualAddress = session.virtualHost
        val protocol = session.protocolVersion
        val clientVersion = Lantern.getGame().minecraftVersionCache.getVersionOrUnknown(protocol, false)
        if (clientVersion == LanternMinecraftVersion.UNKNOWN) {
            Lantern.getLogger().debug("Client with unknown protocol version {} pinged the server.", protocol)
        }
        val client = LanternStatusClient(address, clientVersion, virtualAddress)
        val players = LanternStatusHelper.createPlayers(server)
        val response = LanternStatusResponse(Lantern.getGame().platform.minecraftVersion,
                description, players, server.favicon)
        val cause = causeOf(WrappedRemoteConnection(session))
        val event = LanternEventFactory.createClientPingServerEvent(cause, client, response)
        EventManager.post(event)

        // Cancelled, we are done here
        if (event.isCancelled) {
            context.channel.close()
            return
        }
        val rootObject = JsonObject()
        val versionObject = JsonObject()
        val serverVersion = response.version as LanternMinecraftVersion
        versionObject.addProperty("name", serverVersion.name)
        versionObject.addProperty("protocol", serverVersion.protocol)
        if (response.players.isPresent) {
            val playersObject = JsonObject()
            playersObject.addProperty("max", players.max)
            playersObject.addProperty("online", players.online)
            val profiles = players.profiles
            if (profiles.isNotEmpty()) {
                val array = JsonArray()
                for (profile in profiles) {
                    val optName = profile.name
                    if (!optName.isPresent)
                        continue
                    val profileObject = JsonObject()
                    profileObject.addProperty("name", optName.get())
                    profileObject.addProperty("id", profile.uniqueId.toString())
                    array.add(profileObject)
                }
                playersObject.add("sample", array)
            }
            rootObject.add("players", playersObject)
        }
        rootObject.add("version", versionObject)
        rootObject.add("description", JsonTextSerializer.serializeToTree(response.description))
        response.favicon.ifPresent { icon -> rootObject.addProperty("favicon", (icon as LanternFavicon).encoded) }
        val fmlObject = JsonObject()
        // Trick the client that the server is fml, we support fml channels anyway
        fmlObject.addProperty("type", "FML")
        // The client shouldn't know the plugins (mods) list
        fmlObject.add("modList", JsonArray())

        // Add the fml info
        rootObject.add("modinfo", fmlObject)
        session.send(StatusResponsePacket(this.gson.toJson(rootObject)))
    }
}
