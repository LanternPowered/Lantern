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
package org.lanternpowered.server.network

import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.CodecException
import io.netty.handler.codec.DecoderException
import io.netty.handler.timeout.TimeoutException
import io.netty.util.AttributeKey
import io.netty.util.AttributeMap
import io.netty.util.ReferenceCountUtil
import io.netty.util.concurrent.ScheduledFuture
import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.api.cause.withContext
import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.data.eq
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.plugin.name
import org.lanternpowered.api.profile.GameProfile
import org.lanternpowered.api.service.serviceOf
import org.lanternpowered.api.text.LiteralText
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.appendNewline
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.text.toPlain
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.api.util.collections.toImmutableCollection
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.api.world.World
import org.lanternpowered.api.world.WorldProperties
import org.lanternpowered.server.LanternServer
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.entity.player.tab.GlobalTabList
import org.lanternpowered.server.event.LanternEventFactory
import org.lanternpowered.server.event.message.sendMessage
import org.lanternpowered.server.network.entity.EntityProtocolManager
import org.lanternpowered.server.network.entity.EntityProtocolTypes
import org.lanternpowered.server.network.packet.BulkPacket
import org.lanternpowered.server.network.packet.HandlerPacket
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.PacketHandler
import org.lanternpowered.server.network.packet.UnknownPacket
import org.lanternpowered.server.network.protocol.Protocol
import org.lanternpowered.server.network.protocol.ProtocolState
import org.lanternpowered.server.network.vanilla.packet.type.DisconnectPacket
import org.lanternpowered.server.network.vanilla.packet.type.KeepAlivePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.BrandPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSettingsPacket
import org.lanternpowered.server.permission.Permissions
import org.lanternpowered.server.profile.LanternGameProfile
import org.lanternpowered.server.util.netty.addChannelFutureListener
import org.lanternpowered.server.world.LanternWorldNew
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.network.ServerPlayerConnection
import org.spongepowered.api.service.ban.Ban
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.whitelist.WhitelistService
import org.spongepowered.api.util.locale.Locales
import org.spongepowered.math.vector.Vector3d
import java.io.IOException
import java.net.InetSocketAddress
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Queue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.TimeUnit
import kotlin.time.seconds

class NetworkSession(
        private val networkManager: NetworkManager,
        val server: LanternServer,
        private val channel: Channel
) : SimpleChannelInboundHandler<Packet>(), ServerPlayerConnection, AttributeMap by channel {

    companion object {

        /**
         * The read timeout.
         */
        val READ_TIMEOUT = 10.seconds

        const val ENCRYPTION = "encryption"
        const val LEGACY_PING = "legacy-ping"
        const val COMPRESSION = "compression"
        const val FRAMING = "framing"
        const val PACKET_ENCODER = "packet_encoder"
        const val PACKET_DECODER = "packet_decoder"
        const val PROCESSOR = "processor"
        const val HANDLER = "handler"

        /**
         * The game profile of the player the last time he joined.
         */
        val PREVIOUS_GAME_PROFILE: AttributeKey<GameProfile> = AttributeKey.valueOf("previous-game-profile")

        /**
         * The attribute key for the FML (Forge Mod Loader) marker.
         */
        val FML_MARKER: AttributeKey<Boolean> = AttributeKey.valueOf("fml-marker")

        private fun currentTime(): Long = System.nanoTime() / 1000000L
    }

    /**
     * Whether the session is closed.
     */
    val isClosed: Boolean
        get() = !this.channel.isActive

    /**
     * A future which is completed when the connection is closed.
     */
    val closeFuture: CompletableFuture<Unit> = CompletableFuture<Unit>()
            .also { future -> this.channel.closeFuture().addChannelFutureListener { future.complete(Unit) } }

    /**
     * The game profile of the player that owns this connection.
     */
    private var _profile: LanternGameProfile? = null

    /**
     * The player that owns this connection.
     */
    private var _player: LanternPlayer? = null

    /**
     * The reason that caused the channel to disconnect.
     */
    @Volatile private var disconnectReason: Text? = null

    /**
     * A queue of incoming messages that must be handled on
     * the synchronous thread.
     */
    private val packetQueue: Queue<HandlerPacket<*>> = ConcurrentLinkedDeque()

    /**
     * The virtual host address.
     */
    private var _virtualHost: InetSocketAddress? = null

    /**
     * The protocol state that is currently active.
     */
    @NettyThreadOnly
    var protocolState = ProtocolState.Handshake

    /**
     * Gets the protocol associated with the current type.
     *
     * @return The protocol
     */
    @NettyThreadOnly
    val protocol: Protocol
        get() = this.protocolState.protocol

    /**
     * A list with all the registered channels.
     */
    var registeredChannels = mutableSetOf<String>()

    /**
     * The latency of the connection.
     */
    @Volatile private var _latency = 0

    /**
     * The time that the last the keep alive message was send.
     */
    @NettyThreadOnly
    private var keepAliveTime = -1L

    /**
     * The keep alive/initial connection timer task.
     */
    @NettyThreadOnly
    private var connectionTask: ScheduledFuture<*>? = null

    /**
     * The protocol version.
     */
    @NettyThreadOnly
    var protocolVersion = -1
        set(value) {
            check(field == -1) { "The protocol version can only be set once." }
            field = value
        }

    private var _locale = Locales.DEFAULT

    /**
     * The locale of the player.
     */
    val locale: Locale
        get() = this._locale

    /**
     * Whether the first client settings packet was received.
     */
    private var firstClientSettingsPacket = false

    /**
     * The network context that is used by the handlers.
     */
    private val networkContext: NetworkContext = object : NetworkContext {
        override val session: NetworkSession
            get() = this@NetworkSession
        override val channel: Channel
            get() = this@NetworkSession.channel
    }

    override fun getProfile(): GameProfile = this._profile ?: error("The game profile is not yet initialized.")
    override fun getPlayer(): LanternPlayer = this._player ?: error("The player is not yet initialized.")
    override fun getLatency(): Int = this._latency
    override fun getAddress(): InetSocketAddress = this.channel.remoteAddress() as InetSocketAddress
    override fun getVirtualHost(): InetSocketAddress = this._virtualHost ?: this.address

    fun setVirtualHost(address: InetSocketAddress) {
        this._virtualHost = address
    }

    fun setProfile(profile: GameProfile) {
        this._profile = profile as LanternGameProfile
    }

    override fun channelRead0(ctx: ChannelHandlerContext, packet: Packet) {
        var actualPacket: Packet = packet
        if (actualPacket is HandlerPacket<*>) {
            actualPacket = actualPacket.packet
        }
        if (actualPacket is ClientSettingsPacket) { // Special case, keep track of the locale
            this._locale = actualPacket.locale
            if (!this.firstClientSettingsPacket) {
                this.firstClientSettingsPacket = true
                // Trigger the init
                packetReceived(HandlerPacket(UnknownPacket, object : PacketHandler<UnknownPacket> {
                    override fun handle(ctx: NetworkContext, packet: UnknownPacket) = finalizePlayer()
                }))
            }
        }
        packetReceived(packet)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        // Pipeline error, just log it
        if (cause is CodecException) {
            this.server.logger.error("A netty pipeline error occurred", cause)
        } else {
            if (cause is IOException) {
                val stack = cause.getStackTrace()
                if (stack.isNotEmpty() && stack[0].toString().startsWith("sun.nio.ch.SocketDispatcher.read0"))
                    return
            }

            // Use the debug level, don't spam the server with errors
            // caused by client disconnection, ...
            this.server.logger.debug("A netty connection error occurred", cause)
            if (cause is TimeoutException) {
                this.closeChannel(translatableTextOf("disconnect.timeout"))
            } else {
                this.closeChannel(translatableTextOf("disconnect.genericReason", "Internal Exception: $cause"))
            }
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        this.networkManager.add(this)
        // If the connection isn't established after 30 seconds,
        // kick the player. 30 seconds is the value used in vanilla
        this.connectionTask = this.channel.eventLoop().schedule({
            this.close(translatableTextOf("multiplayer.disconnect.slow_login"))
        }, 30, TimeUnit.SECONDS)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        this.networkManager.remove(this)
        // The player probably just left the server
        if (this.disconnectReason == null) {
            if (this.channel.isOpen) {
                this.disconnectReason = translatableTextOf("disconnect.endOfStream")
            } else {
                this.disconnectReason = translatableTextOf("multiplayer.disconnect.generic")
            }
        }
        val player = this._player
        if (player != null) {
            this.server.syncExecutor.execute(this::leavePlayer)
            val name = this.profile.name.orElse("Unknown")
            val address = this.channel.remoteAddress()
            val reason = this.disconnectReason!!.toPlain()
            this.server.logger.debug("$name ($address) disconnected. Reason: $reason")
        } else if (this.protocolState != ProtocolState.Status) { // Ignore the status requests
            val profile = this._profile
            val name = if (profile == null) "" else " (${profile.name.orElse("Unknown")})"
            val address = this.channel.remoteAddress()
            val reason = this.disconnectReason!!.toPlain()
            this.server.logger.debug("A player$name failed to join from $address. Reason: $reason")
        }
        this.connectionTask?.cancel(true)
        this.connectionTask = null
    }

    @NettyThreadOnly
    private fun initKeepAliveTask() {
        this.connectionTask?.cancel(true)
        this.connectionTask = this.channel.eventLoop().scheduleAtFixedRate({
            val protocolState = this.protocolState
            if (protocolState == ProtocolState.Play) {
                val time = currentTime()
                if (this.keepAliveTime == -1L) {
                    this.keepAliveTime = time
                    send(KeepAlivePacket(time))
                } else {
                    this.close(translatableTextOf("disconnect.timeout"))
                }
            }
        }, 0, 15, TimeUnit.SECONDS)
    }

    private fun handleKeepAlive(message: KeepAlivePacket) {
        if (this.keepAliveTime == message.time) {
            val time = currentTime()
            var latency = this._latency
            latency = ((latency * 3 + (time - this.keepAliveTime)) / 4).toInt()
            // Calculate the latency
            this._latency = latency
            this.keepAliveTime = -1L
            val profile = this._profile
            if (profile != null) {
                // Trigger the init
                packetReceived(HandlerPacket(UnknownPacket, object : PacketHandler<UnknownPacket> {
                    override fun handle(ctx: NetworkContext, packet: UnknownPacket) {
                        GlobalTabList[profile]?.setLatency(latency)
                    }
                }))
            }
        }
    }

    /**
     * Handles the inbound [Packet] with the specified [PacketHandler].
     *
     * @param handler The handler
     * @param packet The packet
     */
    private fun handlePacket(handler: PacketHandler<*>, packet: Packet) {
        try {
            @Suppress("UNCHECKED_CAST")
            (handler as PacketHandler<Packet>).handle(this.networkContext, packet)
        } catch (t: Throwable) {
            this.server.logger.error("Error while handling $packet", t)
        } finally {
            ReferenceCountUtil.release(packet)
        }
    }

    /**
     * Called when the server received a packet from the client.
     *
     * @param packet The packet
     */
    @NettyThreadOnly
    fun packetReceived(packet: Packet) {
        if (packet == UnknownPacket)
            return
        when (packet) {
            is KeepAlivePacket -> {
                // Special case
                this.handleKeepAlive(packet)
            }
            is BulkPacket -> {
                packet.packets.forEach(::packetReceived)
            }
            is HandlerPacket<*> -> {
                when (packet.handleThread) {
                    HandlerPacket.HandleThread.NETTY -> handlePacket(packet.handler, packet.packet)
                    HandlerPacket.HandleThread.ASYNC -> this.server.asyncExecutor.execute { handlePacket(packet.handler, packet.packet) }
                    else -> this.packetQueue.add(packet)
                }
            }
            else -> {
                val packetClass: Class<out Packet> = packet.javaClass
                val registration = this.protocol.inbound.byType(packetClass)
                        ?: throw DecoderException("Failed to find a packet registration for ${packetClass.name}!")
                val handler = registration.handler
                if (handler != null) {
                    @Suppress("UNCHECKED_CAST")
                    handler as PacketHandler<Packet>
                    if (NettyThreadOnlyHelper.isHandlerNettyThreadOnly(handler.javaClass)) {
                        this.handlePacket(handler, packet)
                    } else {
                        this.packetQueue.add(HandlerPacket(packet, handler))
                    }
                }
            }
        }
    }

    /**
     * Updates the session. This should be called from the main thread.
     */
    fun update() {
        while (true) {
            val packet = this.packetQueue.poll() ?: break
            this.handlePacket(packet.handler, packet.packet)
        }
    }

    /**
     * Closes the channel with a specific disconnect reason, this doesn't
     * send a disconnect message to the client, it just closes the connection.
     *
     * @param reason The reason
     */
    @NettyThreadOnly
    private fun closeChannel(reason: Text) {
        this.disconnectReason = reason
        this.channel.close()
    }

    /**
     * Sends a [Packet] and returns the [ChannelFuture].
     *
     * @param packet The message
     * @return The channel future
     */
    fun sendWithFuture(packet: Packet): ChannelFuture {
        if (!this.channel.isActive)
            return this.channel.newPromise()
        ReferenceCountUtil.retain(packet)
        // Write the message and add a exception handler
        return this.channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
    }

    /**
     * Sends a array of [Packet]s and returns the [ChannelFuture].
     *
     * @param packets The messages
     * @return The channel future
     */
    fun sendWithFuture(vararg packets: Packet): ChannelFuture =
            this.sendWithFuture(packets.asList())

    /**
     * Sends a iterable of [Packet]s.
     *
     * @param packets The packets to send
     */
    fun sendWithFuture(packets: Iterable<Packet>): ChannelFuture {
        val itr = packets.iterator()
        if (!itr.hasNext())
            return this.channel.voidPromise()
        val promise = this.channel.newPromise()
        if (!this.channel.isActive)
            return promise
        promise.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE)
        var packet = itr.next()
        // Don't bother checking if we are in the event loop,
        // there is only one message.
        if (!itr.hasNext()) {
            this.channel.writeAndFlush(packet, promise)
        } else {
            val eventLoop = this.channel.eventLoop()
            for (packet0 in packets)
                ReferenceCountUtil.retain(packet0)
            val voidPromise = this.channel.voidPromise()
            if (eventLoop.inEventLoop()) {
                while (true) {
                    val next = itr.hasNext()
                    // Only use a normal channel promise for the last message
                    this.channel.write(packet, if (next) voidPromise else promise)
                    if (!next)
                        break
                    packet = itr.next()
                }
                this.channel.flush()
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                val packets0 = packets.toImmutableCollection()
                eventLoop.submit {
                    val itr0 = packets0.iterator()
                    do {
                        val packet0 = itr0.next()
                        // Only use a normal channel promise for the last message
                        this.channel.writeAndFlush(packet0, if (itr0.hasNext()) voidPromise else promise)
                    } while (itr0.hasNext())
                    this.channel.flush()
                }
            }
        }
        return promise
    }

    /**
     * Sends a [Packet].
     *
     * @param packet The message
     */
    fun send(packet: Packet) {
        if (!this.channel.isActive)
            return
        ReferenceCountUtil.retain(packet)
        // Thrown exceptions will be delegated through the exceptionCaught method
        this.channel.writeAndFlush(packet, this.channel.voidPromise())
    }

    /**
     * Sends a array of [Packet]s.
     *
     * @param packets The messages
     */
    fun send(vararg packets: Packet) =
            this.send(packets.toImmutableList())

    /**
     * Sends a iterable of [Packet]s.
     *
     * @param packets The packets to send
     */
    fun send(packets: Iterable<Packet>) {
        val itr = packets.iterator()
        if (!itr.hasNext())
            return
        val packet = itr.next()
        // Don't bother checking if we are in the event loop,
        // there is only one message.
        val voidPromise = this.channel.voidPromise()
        if (!itr.hasNext()) {
            this.channel.writeAndFlush(packet, voidPromise)
        } else {
            val eventLoop = this.channel.eventLoop()
            packets.forEach { packet0 -> ReferenceCountUtil.retain(packet0) }
            if (eventLoop.inEventLoop()) {
                for (packet0 in packets)
                    this.channel.write(packet0, voidPromise)
                this.channel.flush()
            } else {
                // If there are more then one message, combine them inside the
                // event loop to reduce overhead of wakeup calls and object creation

                // Create a copy of the list, to avoid concurrent modifications
                val packets0 = packets.toImmutableCollection()
                eventLoop.submit {
                    for (packet0 in packets0)
                        this.channel.write(packet0, voidPromise)
                    this.channel.flush()
                }
            }
        }
    }

    override fun close() {
        this.close(textOf("Unknown reason."))
    }

    override fun close(reason: Text) {
        this.tryClose(reason)
    }

    fun tryClose(reason: Text): Boolean {
        if (this.disconnectReason != null)
            return false
        this.disconnectReason = reason
        if (this.channel.isActive &&
                (this.protocolState == ProtocolState.Play || this.protocolState == ProtocolState.Login)) {
            this.sendWithFuture(DisconnectPacket(reason)).addListener(ChannelFutureListener.CLOSE)
        } else {
            this.channel.close()
        }
        return true
    }

    /**
     * Pre initializes the [LanternPlayer], after this state we need
     * to wait for the client to send a [ClientSettingsPacket]
     * so that we have the [Locale] before we start sending translated
     * [Text] objects.
     */
    fun initPlayer() {
        initKeepAliveTask()

        val player = LanternPlayer(this.profile, this)
        player.networkId = EntityProtocolManager.acquireEntityId()
        this._player = player

        // Actually too early to send this, but we want to trigger
        // the client settings to be send to the server, respawn
        // messages will be send afterwards with the proper values

        // TODO: Send join packet
        /*
        send(PlayerJoinPacket(GameModes.SURVIVAL.get(), DimensionTypes.OVERWORLD.get(),
                this.player.networkId, getServer().getMaxPlayers(), false, false, false,
                this.player.serverViewDistance, true, 0L))*/
    }

    /**
     * Is called when the [LanternPlayer] leaves the
     * server and needs to be cleaned up.
     */
    private fun leavePlayer() {
        val player = this.player
        if (player.nullableWorld == null)
            return

        val causeStack = CauseStack.current()
        causeStack.pushCause(player)

        // Close the open container
        player.inventoryContainerSession.setRawOpenContainer(causeStack, null)

        val quitMessage = translatableTextOf("multiplayer.player.left", player.name)
        val audience = this.server.broadcastAudience

        val event = LanternEventFactory.createServerSideConnectionEventDisconnect(
                causeStack.currentCause, audience, audience, quitMessage, quitMessage, this, player, false)
        EventManager.post(event)

        event.sendMessage()
        causeStack.popCause()

        // Remove the proxy user from the player and save the player data
        player.release()
    }

    /**
     * Finally initializes the [LanternPlayer] instance
     * and spawns it in a world if permitted to join
     * the server.
     */
    private fun finalizePlayer() {
        val player = this.player
        player.protocolType = EntityProtocolTypes.PLAYER
        var world: World? = player.nullableWorld
        if (world == null) {
            val worldManager = this.server.worldManager
            var worldProperties: WorldProperties? = player.userWorld
            var fixSpawnLocation = false
            if (worldProperties == null) {
                this.server.logger.warn("The player [${player.name}] attempted to login in a non-existent world, this is not possible "
                        + "so we have moved them to the default's world spawn point.")
                worldProperties = worldManager.defaultProperties.get()
                fixSpawnLocation = true
            } else if (!worldProperties.isEnabled) {
                this.server.logger.warn("The player [${player.name}] attempted to login in a unloaded and not-enabled world " +
                        "[${worldProperties.key}], this is not possible so we have moved them to the default's world spawn point.")
                worldProperties = worldManager.defaultProperties.get()
                fixSpawnLocation = true
            }
            // TODO: Don't block while loading world
            world = worldManager.loadWorld(worldProperties).get()
            // Use the raw method to avoid triggering any network messages
            player.setRawWorld(world)
            player.userWorld = null
            if (fixSpawnLocation) {
                // TODO: Use a proper spawn position
                player.setRawPosition(Vector3d(0.0, 100.0, 0.0))
                player.setRawRotation(Vector3d(0.0, 0.0, 0.0))
            }
        }

        val banService = Sponge.getServiceProvider().provide(BanService::class.java).get()
        // Check whether the player is banned and kick if necessary
        var ban: Ban? = banService.getBanFor(_profile).orElse(null)
        if (ban == null) {
            val address = this.channel.remoteAddress()
            if (address is InetSocketAddress)
                ban = banService.getBanFor(address.address).orElse(null)
        }
        // The kick reason
        var kickReason: Text? = null
        if (ban != null) {
            val expirationDate = ban.expirationDate.orNull()
            val reason = ban.reason.orNull()

            // Generate the kick message
            val builder = LiteralText.builder()
            if (ban is Ban.Profile) {
                builder.append(translatableTextOf("multiplayer.disconnect.ban.banned"))
            } else {
                builder.append(translatableTextOf("multiplayer.disconnect.ban.ip_banned"))
            }
            // There is optionally a reason
            if (reason != null)
                builder.appendNewline().append(translatableTextOf("multiplayer.disconnect.ban.reason", reason))
            // And an expiration date if present
            if (expirationDate != null) {
                val pattern = translatableTextOf("multiplayer.disconnect.ban.expiration_date_format").toPlain()
                val formatter = DateTimeFormatter.ofPattern(pattern)
                val formatted = formatter.format(expirationDate)
                builder.appendNewline().append(translatableTextOf("multiplayer.disconnect.ban.expiration", formatted))
            }
            kickReason = builder.build()
        } else if (!isWhitelisted(this.profile)) { // Check for white-list
            kickReason = translatableTextOf("multiplayer.disconnect.not_whitelisted")
            // Check whether the server is full
        } else if (this.server.unsafePlayers.size >= this.server.maxPlayers
                && !canBypassPlayerLimit(this.profile)) {
            kickReason = translatableTextOf("multiplayer.disconnect.server_full")
        }

        val message = kickReason ?: translatableTextOf("multiplayer.disconnect.not_allowed_to_join")

        val cause = causeOf(this).withContext(CauseContextKeys.PLAYER, player)
        val fromLocation = player.location
        val fromRotation = player.rotation
        val loginEvent = LanternEventFactory.createServerSideConnectionEventLogin(cause, message, message,
                fromLocation, fromLocation, fromRotation, fromRotation, this, player.user, false)
        if (kickReason != null)
            loginEvent.isCancelled = true

        EventManager.post(loginEvent)
        if (loginEvent.isCancelled) {
            this.close(loginEvent.message)
            return
        }
        this.server.logger.debug("The player ${player.name} successfully to joined from ${this.channel.remoteAddress()}.")

        // Update the first join and last played data
        val lastJoined = Instant.now()
        player.offer(Keys.LAST_DATE_PLAYED, lastJoined)
        if (!player.get(Keys.FIRST_DATE_JOINED).isPresent)
            player.offer(Keys.FIRST_DATE_JOINED, lastJoined)

        val toLocation = loginEvent.toLocation
        world = toLocation.world as LanternWorldNew
        val config = world.properties.config

        // Update the game mode if necessary
        if (config.gameMode.forced || player.get(Keys.GAME_MODE).get() eq GameModes.NOT_SET)
            player.offer(Keys.GAME_MODE, config.gameMode.mode)

        // Send the server brand
        this.send(BrandPacket(this.server.game.lanternPlugin.name))

        // Reset the raw world
        player.setRawWorld(null)
        // Set the transform, this will trigger the initial
        // network messages to be send
        player.setLocationAndRotation(loginEvent.toLocation, loginEvent.toRotation)

        val previousProfile = this.channel.attr(PREVIOUS_GAME_PROFILE).getAndSet(null)
        val joinMessage = if (previousProfile != null && previousProfile.name.isPresent && previousProfile.name.get() != player.name) {
            translatableTextOf("multiplayer.player.joined.renamed", player.name, previousProfile.name.get())
        } else {
            translatableTextOf("multiplayer.player.joined", player.name)
        }
        val audience = this.server.broadcastAudience
        val joinEvent = LanternEventFactory.createServerSideConnectionEventJoin(cause, audience, audience,
                joinMessage, joinMessage, this, player, false)
        EventManager.post(joinEvent)
        joinEvent.sendMessage()

        val resourcePack = this.server.defaultResourcePack.orNull()
        if (resourcePack != null)
            player.sendResourcePack(resourcePack)

        player.resetIdleTime()
    }

    private fun canBypassPlayerLimit(gameProfile: GameProfile): Boolean {
        val permissionService = serviceOf<PermissionService>() ?: return false
        return permissionService.userSubjects
                .getSubject(gameProfile.uniqueId.toString())
                .map { subject -> subject.hasPermission(Permissions.Login.BYPASS_PLAYER_LIMIT_PERMISSION) }
                .orElse(false)
    }

    private fun isWhitelisted(gameProfile: GameProfile): Boolean {
        if (!this.server.hasWhitelist())
            return true
        val whitelistService = serviceOf<WhitelistService>() ?: return false
        if (whitelistService.isWhitelisted(gameProfile))
            return true
        val permissionService = serviceOf<PermissionService>() ?: return false
        return permissionService.userSubjects
                .getSubject(gameProfile.uniqueId.toString())
                .map { subject -> subject.hasPermission(Permissions.Login.BYPASS_WHITELIST_PERMISSION) }
                .orElse(false)
    }
}
