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
package org.lanternpowered.server.network.vanilla.packet.handler.handshake

import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Multimap
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.netty.handler.codec.CodecException
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.toText
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.ProxyType
import org.lanternpowered.server.network.packet.handler.Handler
import org.lanternpowered.server.network.protocol.ProtocolState
import org.lanternpowered.server.network.vanilla.packet.handler.login.LoginStartHandler
import org.lanternpowered.server.network.vanilla.packet.type.handshake.HandshakePacket
import org.lanternpowered.server.profile.LanternGameProfile
import org.lanternpowered.server.profile.LanternProfileProperty
import org.lanternpowered.server.util.UUIDHelper
import org.lanternpowered.server.util.gson.fromJson
import org.spongepowered.api.profile.property.ProfileProperty
import java.net.InetSocketAddress

object HandshakeHandler : Handler<HandshakePacket> {

    private const val fmlMarker = "\u0000FML\u0000"
    private val gson = Gson()

    override fun handle(context: NetworkContext, packet: HandshakePacket) {
        val nextState = ProtocolState.getFromId(packet.nextState).orNull()
        val session = context.session
        if (nextState == null) {
            session.close(translatableTextOf("Unknown protocol state! ($nextState)"))
            return
        }
        session.protocolState = nextState
        if (nextState != ProtocolState.LOGIN && nextState != ProtocolState.STATUS) {
            session.close(translatableTextOf("Received a unexpected handshake message! ($nextState)"))
            return
        }
        val proxyType = context.game.config.server.proxy.type
        var hostname = packet.hostname
        val virtualAddress: InetSocketAddress
        when (proxyType) {
            ProxyType.WATERFALL, ProxyType.BUNGEE_CORD, ProxyType.VELOCITY -> {
                var split = hostname.split("\u0000\\|".toRegex(), 2).toTypedArray()

                // Check for a fml marker
                session.attr(NetworkSession.FML_MARKER).set(split.size == 2 == split[1].contains(fmlMarker))
                split = split[0].split("\u0000").toTypedArray()
                if (split.size == 3 || split.size == 4) {
                    virtualAddress = InetSocketAddress(split[1], packet.port)
                    val uniqueId = UUIDHelper.fromFlatString(split[2])
                    val properties = if (split.size == 4) {
                        try {
                            LanternProfileProperty.createPropertiesMapFromJson(gson.fromJson(split[3]))
                        } catch (e: Exception) {
                            session.close(textOf("Invalid ${proxyType.displayName} proxy data format."))
                            throw CodecException(e)
                        }
                    } else {
                        LinkedHashMultimap.create()
                    }
                    session.attr(LoginStartHandler.SPOOFED_GAME_PROFILE).set(LanternGameProfile(uniqueId, null, properties))
                } else {
                    session.close(textOf("Please enable client detail forwarding (also known as \"ip forwarding\") on "
                            + "your proxy if you wish to use it on this server, and also make sure that you joined through the proxy."))
                    return
                }
            }
            ProxyType.LILY_PAD -> {
                virtualAddress = try {
                    val jsonObject = gson.fromJson<JsonObject>(hostname)
                    val securityKey = context.game.config.server.proxy.securityKey
                    // Validate the security key
                    if (securityKey.isNotEmpty() && jsonObject["s"].asString != securityKey) {
                        session.close(textOf("Proxy security key mismatch"))
                        Lantern.getLogger().warn("Proxy security key mismatch for the player {}", jsonObject["n"].asString)
                        return
                    }
                    val name = jsonObject["n"].asString
                    val uniqueId = UUIDHelper.fromFlatString(jsonObject["u"].asString)
                    val properties: Multimap<String, ProfileProperty> = LinkedHashMultimap.create()
                    if (jsonObject.has("p")) {
                        val jsonArray = jsonObject.getAsJsonArray("p")
                        var i = 0
                        while (i < jsonArray.size()) {
                            val property = jsonArray[i].asJsonObject
                            val propertyName = property["n"].asString
                            val propertyValue = property["v"].asString
                            val propertySignature = if (property.has("s")) property["s"].asString else null
                            properties.put(propertyName, LanternProfileProperty(propertyName, propertyValue, propertySignature))
                            i++
                        }
                    }
                    session.attr(LoginStartHandler.SPOOFED_GAME_PROFILE).set(LanternGameProfile(uniqueId, name, properties))
                    session.attr(NetworkSession.FML_MARKER).set(false)
                    val port = jsonObject["rP"].asInt
                    val host = jsonObject["h"].asString
                    InetSocketAddress(host, port)
                } catch (e: Exception) {
                    session.close(textOf("Invalid ${proxyType.displayName} proxy data format."))
                    throw CodecException(e)
                }
            }
            ProxyType.NONE -> {
                val index = hostname.indexOf(this.fmlMarker)
                session.attr(NetworkSession.FML_MARKER).set(index != -1)
                if (index != -1) {
                    hostname = hostname.substring(0, index)
                }
                virtualAddress = InetSocketAddress(hostname, packet.port)
            }
        }
        session.virtualHost = virtualAddress
        session.protocolVersion = packet.protocolVersion
        if (nextState == ProtocolState.LOGIN) {
            val version = context.game.platform.minecraftVersion
            if (packet.protocolVersion < version.protocol) {
                session.close(translatableTextOf("multiplayer.disconnect.outdated_client", version.name.toText()))
            } else if (packet.protocolVersion > version.protocol) {
                session.close(translatableTextOf("multiplayer.disconnect.outdated_server", version.name.toText()))
            }
        }
    }
}
