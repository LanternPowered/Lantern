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
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.handler.login

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.message.handler.Handler
import org.lanternpowered.server.network.pipeline.MessageEncryptionHandler
import org.lanternpowered.server.network.vanilla.message.type.login.LoginEncryptionResponseMessage
import org.lanternpowered.server.network.vanilla.message.type.login.LoginFinishMessage
import org.lanternpowered.server.profile.LanternGameProfile
import org.lanternpowered.server.profile.LanternProfileProperty
import org.lanternpowered.server.text.translation.TranslationHelper
import org.lanternpowered.server.util.UUIDHelper
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.event.message.MessageEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.URL
import java.net.URLEncoder
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class LoginEncryptionResponseHandler : Handler<LoginEncryptionResponseMessage> {

    private val gson = Gson()

    override fun handle(context: NetworkContext, message: LoginEncryptionResponseMessage) {
        val session = context.session
        val privateKey = session.server.keyPair.private

        // Create rsaCipher
        val rsaCipher: Cipher
        rsaCipher = try {
            Cipher.getInstance("RSA")
        } catch (e: GeneralSecurityException) {
            Lantern.getLogger().error("Could not initialize RSA cipher", e)
            session.disconnect(TranslationHelper.t("Unable to initialize RSA cipher."))
            return
        }

        // Decrypt shared secret
        val sharedSecret: SecretKey
        sharedSecret = try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey)
            SecretKeySpec(rsaCipher.doFinal(message.sharedSecret), "AES")
        } catch (e: Exception) {
            Lantern.getLogger().warn("Could not decrypt shared secret", e)
            session.disconnect(TranslationHelper.t("Unable to decrypt shared secret."))
            return
        }

        // Decrypt verify token
        val verifyToken: ByteArray
        verifyToken = try {
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey)
            rsaCipher.doFinal(message.verifyToken)
        } catch (e: Exception) {
            Lantern.getLogger().warn("Could not decrypt verify token", e)
            session.disconnect(TranslationHelper.t("Unable to decrypt verify token."))
            return
        }
        val authData = context.channel.attr(LoginStartHandler.AUTH_DATA).getAndSet(null)

        // Check verify token
        if (!verifyToken.contentEquals(authData.verifyToken)) {
            session.disconnect(TranslationHelper.t("Invalid verify token."))
            return
        }

        // Initialize stream encryption
        session.channel.pipeline().replace(NetworkSession.ENCRYPTION, NetworkSession.ENCRYPTION,
                MessageEncryptionHandler(sharedSecret))

        // Create hash for auth
        val hash: String
        hash = try {
            val digest = MessageDigest.getInstance("SHA-1")
            digest.update(authData.sessionId.toByteArray())
            digest.update(sharedSecret.getEncoded())
            digest.update(session.server.keyPair.public.encoded)

            // BigInteger takes care of sign and leading zeroes
            BigInteger(digest.digest()).toString(16)
        } catch (e: NoSuchAlgorithmException) {
            Lantern.getLogger().error("Unable to generate SHA-1 digest", e)
            session.disconnect(TranslationHelper.t("Failed to hash login data."))
            return
        }
        var preventProxiesIp: String? = null
        if (Lantern.getGame().globalConfig.shouldPreventProxyConnections()) {
            val address = context.session.address.address
            if (!isLocalAddress(address)) { // Ignore local addresses, they will always fail
                preventProxiesIp = try {
                    URLEncoder.encode(address.hostAddress, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    Lantern.getLogger().error("Failed to encode the ip address to prevent proxies.", e)
                    session.disconnect(TranslationHelper.t("Something funky happened."))
                    return
                }
            }
        }
        val preventProxiesIp1 = preventProxiesIp
        Lantern.getAsyncScheduler().submit { performAuth(session, authData.username, hash, preventProxiesIp1) }
    }

    private fun performAuth(session: NetworkSession, username: String, hash: String, preventProxiesIp: String?) {
        val postUrl = AUTH_BASE_URL + "?username=" + username + "&serverId=" + hash +
                if (preventProxiesIp == null) "" else "?ip=$preventProxiesIp"
        try {
            // Authenticate
            val connection = URL(postUrl).openConnection()
            val json = connection.getInputStream().use { `is` ->
                if (`is`.available() == 0) {
                    session.disconnect(TranslationHelper.t("Invalid username or session id!"))
                    return
                }
                try {
                    this.gson.fromJson(InputStreamReader(`is`), JsonObject::class.java)
                } catch (e: Exception) {
                    Lantern.getLogger().warn("Username \"{}\" failed to authenticate!", username)
                    session.disconnect(TranslationHelper.t("multiplayer.disconnect.unverified_username"))
                    return
                }
            }

            val name = json["name"].asString
            val id = json["id"].asString

            val uuid = try {
                UUIDHelper.fromFlatString(id)
            } catch (e: IllegalArgumentException) {
                Lantern.getLogger().error("Returned authentication UUID invalid: {}", id, e)
                session.disconnect(TranslationHelper.t("Invalid UUID."))
                return
            }

            val properties = LanternProfileProperty
                    .createPropertiesMapFromJson(json.getAsJsonArray("properties"))
            val gameProfile = LanternGameProfile(uuid, name, properties)
            Lantern.getLogger().info("Finished authenticating.")
            val cause = Cause.of(EventContext.empty(), session, gameProfile)
            val event: ClientConnectionEvent.Auth = LanternEventFactory.createClientConnectionEventAuth(cause, session,
                    MessageEvent.MessageFormatter(TranslationHelper.t("multiplayer.disconnect.not_allowed_to_join")), gameProfile, false)
            EventManager.post(event)
            if (event.isCancelled) {
                session.disconnect(if (event.isMessageCancelled) TranslationHelper.t("multiplayer.disconnect.generic") else event.message)
            } else {
                session.queueReceivedMessage(LoginFinishMessage(gameProfile))
            }
        } catch (e: Exception) {
            Lantern.getLogger().error("Error in authentication thread", e)
            session.disconnect(TranslationHelper.t("Internal error during authentication."))
        }
    }

    companion object {

        private const val AUTH_BASE_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined"

        // https://stackoverflow.com/questions/2406341/how-to-check-if-an-ip-address-is-the-local-host-on-a-multi-homed-system
        private fun isLocalAddress(address: InetAddress): Boolean {
            // Check if the address is a valid special local or loop back
            return if (address.isAnyLocalAddress || address.isLoopbackAddress) {
                true
            } else try {
                NetworkInterface.getByInetAddress(address) != null
            } catch (e: SocketException) {
                false
            }
            // Check if the address is defined on any interface
        }
    }
}
