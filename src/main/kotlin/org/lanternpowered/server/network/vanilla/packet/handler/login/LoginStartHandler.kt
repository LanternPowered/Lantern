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
package org.lanternpowered.server.network.vanilla.packet.handler.login

import io.netty.util.AttributeKey
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.NettyThreadOnly
import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginEncryptionRequestPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginFinishPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginStartPacket
import org.lanternpowered.server.profile.LanternGameProfile
import org.lanternpowered.server.util.EncryptionHelper
import java.util.UUID
import java.util.concurrent.ExecutionException

object LoginStartHandler : PacketHandler<LoginStartPacket> {

    // The data that will be used for authentication.
    val AUTH_DATA: AttributeKey<LoginAuthData> = AttributeKey.valueOf<LoginAuthData>("login-auth-data")

    // The spoofed game profile that may be provided by proxies
    @JvmField
    val SPOOFED_GAME_PROFILE: AttributeKey<LanternGameProfile> = AttributeKey.valueOf<LanternGameProfile>("spoofed-game-profile")

    @NettyThreadOnly
    override fun handle(ctx: NetworkContext, packet: LoginStartPacket) {
        val session = ctx.session
        val username = packet.username
        if (session.server.onlineMode) {
            // Convert to X509 format
            val publicKey = session.server.keyPair.public.encoded
            val verifyToken = EncryptionHelper.generateVerifyToken()

            // Store the auth data
            session.attr(AUTH_DATA).set(LoginAuthData(username, verifyToken))
            // Send created request message and wait for the response
            session.send(LoginEncryptionRequestPacket(publicKey, verifyToken))
        } else {
            // Remove the encryption handler placeholder
            ctx.channel.pipeline().remove(NetworkSession.ENCRYPTION)
            var profile = ctx.channel.attr(SPOOFED_GAME_PROFILE).getAndSet(null)
            profile = if (profile != null) {
                LanternGameProfile(profile.uniqueId, username, profile.propertyMap)
            } else {
                // Try the online id first
                try {
                    Lantern.getGame().gameProfileManager[username].get() as LanternGameProfile
                } catch (e: ExecutionException) {
                    // Generate a offline id
                    val uniqueId = UUID.nameUUIDFromBytes("OfflinePlayer:$username".toByteArray(Charsets.UTF_8))
                    LanternGameProfile(uniqueId, username)
                }
            }
            session.packetReceived(LoginFinishPacket(profile))
        }
    }
}
