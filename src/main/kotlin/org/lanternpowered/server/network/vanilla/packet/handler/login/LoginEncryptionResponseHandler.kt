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
package org.lanternpowered.server.network.vanilla.packet.handler.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.WrappedServerSideConnection
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.pipeline.PacketEncryptionHandler
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginEncryptionResponsePacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginFinishPacket
import org.lanternpowered.server.profile.LanternGameProfile
import org.lanternpowered.server.profile.LanternProfileProperty
import org.lanternpowered.server.util.EncryptionHelper
import org.lanternpowered.server.util.InetAddressHelper
import org.lanternpowered.server.util.UUIDHelper
import org.lanternpowered.server.util.future.thenAsync
import org.lanternpowered.server.util.gson.fromJson
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture
import javax.crypto.spec.SecretKeySpec

object LoginEncryptionResponseHandler : PacketHandler<LoginEncryptionResponsePacket> {

    private const val authBaseUrl = "https://sessionserver.mojang.com/session/minecraft/hasJoined"
    private val gson = Gson()

    override fun handle(context: NetworkContext, packet: LoginEncryptionResponsePacket) {
        val session = context.session
        val keyPair = session.server.keyPair

        val authData: LoginAuthData? = context.channel.attr(LoginStartHandler.AUTH_DATA).getAndSet(null)
        checkNotNull(authData) { "No login auth data." }

        val decryptedVerifyToken = EncryptionHelper.decryptRsa(keyPair, packet.verifyToken)
        check(!(decryptedVerifyToken contentEquals authData.verifyToken)) { "Invalid verify token." }

        val decryptedSharedSecret = EncryptionHelper.decryptRsa(keyPair, packet.sharedSecret)
        val serverId = EncryptionHelper.generateServerId(decryptedSharedSecret, keyPair.public)
        val preventProxiesIp = this.preventProxiesIp(context.session)

        val secretKey = SecretKeySpec(decryptedSharedSecret, "AES")
        val connection = WrappedServerSideConnection(context.session)

        this.requestAuth(context, authData.username, serverId, preventProxiesIp)
                .thenAsync(session.server.syncExecutor) { profile ->
                    val cause = causeOf(session, profile)
                    val originalMessage = translatableTextOf("multiplayer.disconnect.not_allowed_to_join")
                    val event = LanternEventFactory.createServerSideConnectionEventAuth(
                            cause, originalMessage, originalMessage, connection, false)
                    EventManager.post(event)
                    if (event.isCancelled) {
                        session.close(event.message)
                        null
                    } else profile
                }
                .thenAsync(context.channel.eventLoop()) { profile ->
                    if (profile == null)
                        return@thenAsync
                    context.channel.pipeline().replace(NetworkSession.ENCRYPTION, NetworkSession.ENCRYPTION,
                            PacketEncryptionHandler(secretKey))
                    session.packetReceived(LoginFinishPacket(profile))
                }
    }

    private fun preventProxiesIp(session: NetworkSession): String? {
        if (!session.server.config.server.preventProxyConnections)
            return null
        val address = session.address.address
        // Ignore local addresses, they will always fail
        if (InetAddressHelper.isLocalAddress(address))
            return null
        return try {
            URLEncoder.encode(address.hostAddress, Charsets.UTF_8)
        } catch (e: UnsupportedEncodingException) {
            throw IllegalStateException("Failed to encode the ip address to prevent proxies.", e)
        }
    }

    private fun requestAuth(
            context: NetworkContext, username: String, serverId: String, preventProxiesIp: String?
    ): CompletableFuture<LanternGameProfile> {
        var url = "$authBaseUrl?username=$username&serverId=$serverId"
        if (preventProxiesIp != null)
            url += "?ip=$preventProxiesIp"
        return context.server.httpClient.get(url, context.channel.eventLoop()).thenApply { response ->
            if (response.body.isEmpty())
                throw IllegalStateException("Invalid username or session id.")
            val json = try {
                gson.fromJson<JsonObject>(response.body)
            } catch (e: Exception) {
                throw IllegalStateException("Username $username failed to authenticate.")
            }

            val name = json["name"].asString
            val id = json["id"].asString
            val uniqueId = UUIDHelper.parseFlatStringOrNull(id) ?: error("Received an invalid uuid: $id")

            val properties = LanternProfileProperty.createPropertiesMapFromJson(
                    json.getAsJsonArray("properties"))
            LanternGameProfile(uniqueId, name, properties)
        }
    }
}
