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
package org.lanternpowered.server

import joptsimple.OptionSet
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.Server
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.service.world.WorldStorageService
import org.lanternpowered.api.util.collections.asUnmodifiableCollection
import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.world.WorldManager
import org.lanternpowered.server.cause.LanternCauseStack
import org.lanternpowered.server.cause.LanternCauseStackManager
import org.lanternpowered.server.config.GlobalConfigObject
import org.lanternpowered.server.console.LanternConsole
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.lanternpowered.server.event.lifecycle.LanternStartedServerEvent
import org.lanternpowered.server.event.lifecycle.LanternStartingServerEvent
import org.lanternpowered.server.event.lifecycle.LanternStoppingServerEvent
import org.lanternpowered.server.network.NetworkManager
import org.lanternpowered.server.network.ProxyType
import org.lanternpowered.server.network.query.QueryServer
import org.lanternpowered.server.network.rcon.EmptyRconService
import org.lanternpowered.server.network.rcon.RconServer
import org.lanternpowered.server.service.world.DefaultWorldStorageService
import org.lanternpowered.server.util.EncryptionHelper
import org.lanternpowered.server.util.ShutdownMonitorThread
import org.lanternpowered.server.util.SyncLanternThread
import org.lanternpowered.server.world.LanternTeleportHelper
import org.lanternpowered.server.world.LanternWorldManager
import org.spongepowered.api.network.status.Favicon
import org.spongepowered.api.profile.GameProfileManager
import org.spongepowered.api.resourcepack.ResourcePack
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.scoreboard.Scoreboard
import org.spongepowered.api.service.rcon.RconService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.channel.MessageChannel
import org.spongepowered.api.user.UserManager
import org.spongepowered.api.world.TeleportHelper
import org.spongepowered.api.world.storage.ChunkLayout
import java.io.Closeable
import java.io.FileNotFoundException
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.nio.file.Files
import java.util.Optional
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

class LanternServerNew : Server {

    private val game: LanternGame = LanternGame
    private lateinit var worldManager: LanternWorldManager
    private lateinit var ioExecutor: ExecutorService
    private lateinit var console: LanternConsole
    private lateinit var networkManager: NetworkManager
    private var queryServer: QueryServer? = null
    private var defaultResourcePack: ResourcePack? = null

    private val playersByUniqueId = concurrentHashMapOf<UUID, LanternPlayer>()
    private val playersByName = concurrentHashMapOf<String, LanternPlayer>()

    /**
     * A live view of all the online players.
     */
    val unsafePlayers: Collection<LanternPlayer> = this.playersByUniqueId.values.asUnmodifiableCollection()

    /**
     * The key pair used for authentication.
     */
    val keyPair = EncryptionHelper.generateRsaKeyPair()

    /**
     * The favicon that shows up in the server list.
     */
    var favicon: Favicon? = null
        private set

    private val shuttingDown = AtomicBoolean()

    val config: GlobalConfigObject
        get() = this.game.config

    val logger: Logger
        get() = this.game.logger

    /**
     * Initializes the game and starts the server.
     */
    fun launch(options: OptionSet) {
        LanternCauseStackManager.setCurrentCauseStack(LanternCauseStack())

        this.console = LanternConsole(this)
        this.console.init()

        this.game.init(options)
        this.game.setServer(this)

        // Initialize the console subject
        this.console.resolveSubject()

        val config = this.game.config

        val worldsDirectory = this.game.gameDirectory.resolve(options.valueOf(LaunchOptions.CONFIG_DIRECTORY) ?: "worlds")
        val worldStorageService = this.game.serviceProvider.register<WorldStorageService> {
            this.game.lanternPlugin to DefaultWorldStorageService(worldsDirectory)
        }

        this.ioExecutor = Executors.newCachedThreadPool() // TODO: Use a specific amount of threads
        this.worldManager = LanternWorldManager(this, this.ioExecutor, worldStorageService)

        this.game.eventManager.post(LanternStartingServerEvent(game, this))

        if (config.server.proxy.type == ProxyType.NONE && !config.server.onlineMode) {
            this.logger.warn("It is not recommend to run the server in offline mode, this allows people to")
            this.logger.warn("choose any username they want. The server will use the account that is attached")
            this.logger.warn("to the username, it doesn't care if it's in offline mode, this will only")
            this.logger.warn("disable the authentication and allow non registered usernames to be used.")
        }

        tryBindServer()

        // Start the RCON server, if enabled
        this.game.serviceProvider.register { this.game.lanternPlugin to startRconServer() }

        // Start the Query server, if enabled
        this.queryServer = startQueryServer()

        loadFavicon()
        loadDefaultResourcePack()

        this.game.eventManager.post(LanternStartedServerEvent(this.game, this))

        // Start reading inputs
        this.console.start()
    }

    private fun tryBindServer() {
        val config = this.config.server
        val address = getBindAddress(config.ip, config.port)

        this.networkManager = NetworkManager(this)

        val future = this.networkManager.init(address)
        val channel = future.awaitUninterruptibly().channel()

        if (!channel.isActive) {
            val cause = future.cause()
            this.logger.error("The server could not bind to the requested address.")
            val message = cause.message
            if (message != null && message.startsWith("Cannot assign requested address")) {
                this.logger.error("The 'server.ip' in your global config file may not be valid.")
                this.logger.error("Unless you are sure you need it, try removing it.")
                this.logger.error(cause.toString())
            } else if (message != null && message.startsWith("Address already in use")) {
                this.logger.error("The address was already in use. Check that no server is")
                this.logger.error("already running on that port. If needed, try killing all")
                this.logger.error("Java processes using Task Manager or similar.")
                this.logger.error(cause.toString())
            } else {
                this.logger.error("An unknown bind error has occurred.", cause)
            }
            exitProcess(1)
        }

        this.logger.info("Successfully bound to: " + channel.localAddress())
    }

    private fun startQueryServer(): QueryServer? {
        val config = this.config.query
        if (!config.enabled)
            return null

        val server = QueryServer(this.game, config.showPlugins)
        val address = getBindAddress(config.ip, config.port)

        this.logger.info("Starting query server and binding it to: $address...")

        val future = server.init(address)
        val channel = future.awaitUninterruptibly().channel()
        if (!channel.isActive) {
            this.logger.warn("Failed to bind query server. Address already in use?")
            return null
        }

        return server
    }

    /**
     * Starts the [RconService].
     */
    private fun startRconServer(): RconService {
        val config = this.config.rcon
        if (!config.enabled)
            return EmptyRconService(config.password)

        val server = RconServer(config.password)
        val address = getBindAddress(config.ip, config.port)

        this.logger.info("Starting rcon server and binding it to: $address...")

        val future = server.init(address)
        val channel = future.awaitUninterruptibly().channel()
        if (!channel.isActive) {
            this.logger.warn("Failed to bind rcon. Address already in use?")
            return EmptyRconService(config.password)
        }

        return server
    }

    private fun loadFavicon() {
        val faviconPath = this.game.gameDirectory.resolve(this.config.server.favicon)
        if (Files.exists(faviconPath)) {
            try {
                this.favicon = Favicon.load(faviconPath)
            } catch (e: IOException) {
                this.logger.error("Failed to load the favicon", e)
            }
        } else {
            try {
                LanternServerNew::class.java.getResourceAsStream("/data/lantern/icon/favicon.png").use { inputStream ->
                    this.favicon = Favicon.load(inputStream)
                }
            } catch (e: IOException) {
                throw IllegalStateException("Failed to load the default favicon.", e)
            }
        }
    }

    private fun loadDefaultResourcePack() {
        val resourcePackPath = this.config.server.defaultResourcePack
        if (resourcePackPath.isEmpty())
            return
        try {
            this.defaultResourcePack = ResourcePack.fromUri(URI.create(resourcePackPath))
        } catch (e: FileNotFoundException) {
            this.logger.warn("Couldn't find a valid resource pack at the location: $resourcePackPath", e)
        }
    }

    override fun getBoundAddress(): Optional<InetSocketAddress> = this.networkManager.address.optional()

    /**
     * Get the socket address to bind to for a specified service.
     *
     * @param port the port to use
     * @return the socket address
     */
    private fun getBindAddress(ip: String, port: Int): InetSocketAddress =
            if (ip.isEmpty()) InetSocketAddress(port) else InetSocketAddress(ip, port)

    override fun shutdown() = shutdown(this.config.server.shutdownMessage)

    override fun shutdown(kickMessage: Text) {
        // Don't shut down twice
        if (!this.shuttingDown.compareAndSet(false, true))
            return
        this.game.eventManager.post(LanternStoppingServerEvent(this.game, this))
        this.ioExecutor.shutdown()

        this.queryServer?.shutdown()

        for (service in this.game.serviceProvider.registrations.map { it.service() }.distinct()) {
            if (service is Closeable)
                service.close()
        }

        // Wait for a while and terminate any rogue threads
        ShutdownMonitorThread(10, TimeUnit.SECONDS).start()
    }

    override fun isDedicatedServer(): Boolean = true
    override fun getGame(): LanternGame = this.game
    override fun getWorldManager(): WorldManager = this.worldManager
    override fun getDefaultResourcePack(): Optional<ResourcePack> = this.defaultResourcePack.optional()
    override fun getTeleportHelper(): TeleportHelper = LanternTeleportHelper

    override fun getMaxPlayers(): Int = this.config.server.maxPlayers
    override fun getMotd(): Text = this.config.server.messageOfTheDay
    override fun getOnlineMode(): Boolean = this.config.server.onlineMode

    override fun hasWhitelist(): Boolean = this.config.server.whitelist
    override fun setHasWhitelist(enabled: Boolean) { this.config.server.whitelist = enabled }

    override fun getPlayerIdleTimeout(): Int = this.config.server.playerIdleTimeout
    override fun setPlayerIdleTimeout(timeout: Int) { this.config.server.playerIdleTimeout = timeout }

    override fun onMainThread(): Boolean =
            Thread.currentThread() is SyncLanternThread

    override fun getOnlinePlayers(): Collection<Player> = this.playersByUniqueId.values.toImmutableList()

    override fun setMessageChannel(channel: MessageChannel?) {
        TODO("Not yet implemented")
    }

    override fun setBroadcastChannel(channel: MessageChannel?) {
        TODO("Not yet implemented")
    }

    override fun getTicksPerSecond(): Double = 20.0

    override fun getUserManager(): UserManager {
        TODO("Not yet implemented")
    }

    override fun getServerScoreboard(): Optional<Scoreboard> {
        TODO("Not yet implemented")
    }

    override fun getScheduler(): Scheduler {
        TODO("Not yet implemented")
    }

    override fun getCauseStackManager(): CauseStackManager = LanternCauseStackManager

    override fun sendMessage(message: Text) {
        for (player in this.playersByUniqueId.values)
            player.sendMessage(message)
    }

    override fun getMessageChannel(): MessageChannel {
        TODO("Not yet implemented")
    }

    override fun getPlayer(uniqueId: UUID): Optional<Player> = this.playersByUniqueId[uniqueId].optional()
    override fun getPlayer(name: String): Optional<Player> = this.playersByName[name].optional()

    override fun getBroadcastChannel(): MessageChannel {
        TODO("Not yet implemented")
    }

    override fun getGameProfileManager(): GameProfileManager {
        TODO("Not yet implemented")
    }

    override fun getRunningTimeTicks(): Int {
        TODO("Not yet implemented")
    }

    override fun getChunkLayout(): ChunkLayout {
        TODO("Not yet implemented")
    }
}
