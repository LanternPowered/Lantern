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
package org.lanternpowered.server.network.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.api.text.serializer.LegacyTextSerializer
import org.lanternpowered.api.text.toPlain
import org.lanternpowered.api.text.toText
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.LanternServer
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.game.version.LanternMinecraftVersion
import org.lanternpowered.server.network.NetworkSession
import org.lanternpowered.server.network.SimpleRemoteConnection
import org.lanternpowered.server.network.status.LanternStatusClient
import org.lanternpowered.server.network.status.LanternStatusHelper
import org.lanternpowered.server.network.status.LanternStatusResponse
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

class LegacyProtocolHandler(private val session: NetworkSession) : ChannelInboundHandlerAdapter() {

    private val server: LanternServer
        get() = this.session.server

    private val logger: Logger
        get() = this.session.server.logger

    override fun channelRead(ctx: ChannelHandlerContext, buf: Any) {
        buf as ByteBuf
        val server = this.session.server
        buf.markReaderIndex()

        // Whether it was a valid legacy message
        var legacy = false
        try {
            val messageId = buf.readUnsignedByte().toInt()
            // Old client's are not so smart, make sure that
            // they don't attempt to login
            if (messageId == 0x02) {
                val protocol = buf.readByte().toInt() // Protocol version
                var value = buf.readShort().toInt()
                // Check the length
                if (value < 0 || value > 16)
                    return
                buf.readBytes(value shl 1) // Username
                value = buf.readShort().toInt()
                // Check the length
                if (value < 0 || value > 255)
                    return
                buf.readBytes(value shl 1) // Host address
                buf.readInt() // Port
                if (buf.readableBytes() > 0)
                    return
                legacy = true
                ctx.disconnect(translatableTextOf("multiplayer.disconnect.outdated_client",
                        this.server.platform.minecraftVersion.name.toText()).toPlain())
                val clientVersion = Lantern.getGame().minecraftVersionCache.getVersionOrUnknown(protocol, true)
                if (clientVersion === LanternMinecraftVersion.UNKNOWN_LEGACY) {
                    this.logger.debug(
                            "Client with unknown legacy protocol version $protocol attempted to join the server.")
                } else {
                    this.logger.debug(
                            "Client with legacy protocol version $protocol (mc-version ${clientVersion.name}) attempted to join the server.")
                }
                return
            }

            // Check for the ping message id.
            if (messageId != 0xfe)
                return
            var readable = buf.readableBytes()
            var full = false

            // The version used to ping the server
            var protocol = V1_3_2_PROTOCOL

            // Versions 1.4 - 1.5.x + 1.6 - Can request full data.
            if (readable > 0) {
                // Is always 1
                if (buf.readUnsignedByte().toInt() != 1)
                    return
                full = true
                protocol = V1_5_2_PROTOCOL
            }

            // The virtual address that was used to join the server
            var virtualAddress: InetSocketAddress? = null

            // Version 1.6 - Used extra data.
            if (readable > 1) {
                if (buf.readUnsignedByte().toInt() != 0xfa)
                    return
                var bytes = ByteArray(buf.readShort().toInt() shl 1)
                buf.readBytes(bytes)
                if (String(bytes, StandardCharsets.UTF_16BE) != "MC|PingHost")
                    return

                // Not used
                buf.readShort()

                // The protocol version is present
                protocol = buf.readUnsignedByte().toInt()

                // There is extra host and port data
                if (protocol >= 73) {
                    bytes = ByteArray(buf.readShort().toInt() shl 1)
                    buf.readBytes(bytes)
                    val host = String(bytes, StandardCharsets.UTF_16BE)
                    val port = buf.readInt()
                    virtualAddress = InetSocketAddress.createUnresolved(host, port)
                }
                readable = buf.readableBytes()
                if (readable > 0) {
                    this.logger.warn("Trailing bytes on a legacy ping message: {}b", readable)
                }
            }

            // The message was successfully decoded as a legacy one
            legacy = true
            val full1 = full
            val protocol1 = protocol
            val virtualAddress1 = virtualAddress

            // Call the event in the main thread
            Lantern.getSyncScheduler().submit {
                val clientVersion = this.server.game.minecraftVersionCache.getVersionOrUnknown(protocol1, true)
                if (clientVersion === LanternMinecraftVersion.UNKNOWN)
                    this.logger.debug("Client with unknown legacy protocol version {} pinged the server.", protocol1)
                val serverVersion = this.server.platform.minecraftVersion
                var description = server.motd
                val address = ctx.channel().remoteAddress() as InetSocketAddress
                val client = LanternStatusClient(address, clientVersion, virtualAddress1)
                val players = LanternStatusHelper.createPlayers(server)
                val response = LanternStatusResponse(serverVersion, description, players, server.favicon)
                val connection = SimpleRemoteConnection.of(ctx.channel(), virtualAddress1)
                val cause = causeOf(connection)
                val event = LanternEventFactory.createClientPingServerEvent(cause, client, response)
                this.server.eventManager.post(event)

                // Cancelled, we are done here
                if (event.isCancelled) {
                    ctx.channel().close()
                    return@submit
                }

                description = response.description
                var online = players.online
                val max = players.max

                // The players should be hidden, this will replace the player count
                // with ???
                if (!response.players.isPresent) {
                    online = -1
                }
                val data = if (full1) {
                    val description0 = getFirstLine(LegacyTextSerializer.serialize(description))
                    // 1. This value is always 1.
                    // 2. The protocol version, just use a value out of range
                    //    of the available ones.
                    // 3. The version/name string of the server.
                    // 4. The motd of the server. In legacy format.
                    // 5. The online players
                    // 6. The maximum amount of players
                    String.format("\u00A7%s\u0000%s\u0000%s\u0000%s\u0000%s\u0000%s",
                            1, 127, response.version.name, description0, online, max)
                } else {
                    val description0 = getFirstLine(description.toPlain())
                    // 1. The motd of the server. In legacy format.
                    // 2. The online players
                    // 3. The maximum amount of players
                    String.format("%s\u00A7%s\u00A7%s",
                            description0, online, max)
                }
                ctx.disconnect(data)
            }
        } catch (ignore: Exception) {
        } finally {
            if (legacy) {
                buf.release()
            } else {
                buf.resetReaderIndex()
                ctx.channel().pipeline().remove(this)
                ctx.fireChannelRead(buf)
            }
        }
    }

    /**
     * Sends a disconnect message to a legacy client and closes the connection.
     *
     * @param message The message
     */
    private fun ChannelHandlerContext.disconnect(message: String) {
        val data = message.toByteArray(StandardCharsets.UTF_16BE)
        val output = alloc().buffer()
        output.writeByte(0xff)
        output.writeShort(data.size shr 1)
        output.writeBytes(data)
        val firstContext: ChannelHandlerContext? = channel().pipeline().firstContext()
        firstContext?.writeAndFlush(output)?.addListener(ChannelFutureListener.CLOSE)
    }

    private fun getFirstLine(value: String): String {
        val i = value.indexOf('\n')
        return if (i == -1) value else value.substring(0, i)
    }

    companion object {
        private const val V1_3_2_PROTOCOL = 39
        private const val V1_5_2_PROTOCOL = 61
    }
}
