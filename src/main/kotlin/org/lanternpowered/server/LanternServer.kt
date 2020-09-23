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

import io.netty.channel.EventLoopGroup
import joptsimple.OptionSet
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.Platform
import org.lanternpowered.api.Server
import org.lanternpowered.api.audience.Audience
import org.lanternpowered.api.cause.CauseStackManager
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.plugin.version
import org.lanternpowered.api.scoreboard.Scoreboard
import org.lanternpowered.api.service.profile.GameProfileService
import org.lanternpowered.api.service.user.UserStorageService
import org.lanternpowered.api.service.world.WorldStorageService
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.collections.asUnmodifiableCollection
import org.lanternpowered.api.util.collections.concurrentHashMapOf
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.world.WorldManager
import org.lanternpowered.api.world.teleport.TeleportHelper
import org.lanternpowered.server.cause.LanternCauseStack
import org.lanternpowered.server.cause.LanternCauseStackManager
import org.lanternpowered.server.config.GlobalConfigObject
import org.lanternpowered.server.console.LanternConsole
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.event.lifecycle.LanternStartedServerEvent
import org.lanternpowered.server.event.lifecycle.LanternStartingServerEvent
import org.lanternpowered.server.event.lifecycle.LanternStoppingServerEvent
import org.lanternpowered.server.network.NetworkManager
import org.lanternpowered.server.network.ProxyType
import org.lanternpowered.server.network.TransportType
import org.lanternpowered.server.network.http.NettyHttpClient
import org.lanternpowered.server.network.query.QueryServer
import org.lanternpowered.server.network.rcon.EmptyRconService
import org.lanternpowered.server.network.rcon.RconServer
import org.lanternpowered.server.profile.LanternGameProfileManager
import org.lanternpowered.server.scheduler.LanternScheduler
import org.lanternpowered.server.scoreboard.LanternScoreboard
import org.lanternpowered.server.service.LanternServiceProvider
import org.lanternpowered.server.service.profile.LanternGameProfileService
import org.lanternpowered.server.service.user.DefaultUserStorageService
import org.lanternpowered.server.service.world.DefaultWorldStorageService
import org.lanternpowered.server.user.LanternUserManager
import org.lanternpowered.server.util.EncryptionHelper
import org.lanternpowered.server.util.ShutdownMonitorThread
import org.lanternpowered.server.util.SyncLanternThread
import org.lanternpowered.server.util.ThreadHelper
import org.lanternpowered.server.util.coroutines.asScheduledExecutorService
import org.lanternpowered.server.util.executor.LanternExecutorService
import org.lanternpowered.server.util.executor.LanternScheduledExecutorService
import org.lanternpowered.server.util.executor.asLanternExecutorService
import org.lanternpowered.server.world.LanternTeleportHelper
import org.lanternpowered.server.world.LanternWorldManager
import org.lanternpowered.server.world.chunk.LanternChunkLayout
import org.spongepowered.api.network.status.Favicon
import org.spongepowered.api.profile.GameProfileManager
import org.spongepowered.api.resourcepack.ResourcePack
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.service.rcon.RconService
import org.spongepowered.api.user.UserManager
import org.spongepowered.api.world.storage.ChunkLayout
import java.io.Closeable
import java.io.FileNotFoundException
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.nio.file.Files
import java.time.Duration
import java.time.Instant
import java.util.Optional
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

class LanternServer : Server {

    private val startTime = Instant.now()
    private val game: LanternGame = LanternGame
    private lateinit var worldManager: LanternWorldManager

    /**
     * The [ExecutorService] that should be used for IO operations.
     */
    lateinit var ioExecutor: LanternExecutorService
        private set

    /**
     * The [ScheduledExecutorService] that should be used for "sync" tasks.
     */
    lateinit var syncExecutor: LanternScheduledExecutorService
        private set

    /**
     * The [ScheduledExecutorService] that should be used for "async" tasks.
     */
    lateinit var asyncExecutor: LanternScheduledExecutorService
        private set

    private lateinit var syncScheduler: LanternScheduler
    private lateinit var asyncScheduler: LanternScheduler

    private lateinit var console: LanternConsole
    private lateinit var networkManager: NetworkManager
    private lateinit var audiences: Iterable<Audience>
    private lateinit var gameProfileManager: GameProfileManager
    private var queryServer: QueryServer? = null
    private var defaultResourcePack: ResourcePack? = null
    @Volatile private var broadcastAudience: Audience = this

    lateinit var userStorageService: UserStorageService
        private set

    lateinit var userManager: LanternUserManager
        private set

    private val playersByUniqueId = concurrentHashMapOf<UUID, LanternPlayer>()
    private val playersByName = concurrentHashMapOf<String, LanternPlayer>()

    private lateinit var nettyBossGroup: EventLoopGroup
    private lateinit var nettyWorkerGroup: EventLoopGroup

    lateinit var httpClient: NettyHttpClient
        private set

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

    val platform: Platform
        get() = this.game.platform

    val eventManager: EventManager
        get() = this.game.eventManager

    val scoreboard: LanternScoreboard = TODO()

    /**
     * Initializes the game and starts the server.
     */
    fun launch(options: OptionSet, mainExecutor: ScheduledExecutorService) {
        LanternCauseStackManager.setCurrentCauseStack(LanternCauseStack())

        this.console = LanternConsole(this)
        this.console.init()

        this.audiences = (this.unsafePlayers.asSequence() + sequenceOf<Audience>(this.console)).asIterable()

        this.asyncExecutor = Dispatchers.Default.asScheduledExecutorService()
        this.asyncScheduler = LanternScheduler(this.asyncExecutor)

        this.game.init(options, this.console, this.asyncScheduler)
        this.game.server = this

        this.showWelcome()

        val config = this.game.config

        val worldsDirectory = this.game.gameDirectory.resolve(options.valueOf(LaunchOptions.WORLDS_DIRECTORY) ?: "worlds")
        val worldStorageService = this.game.serviceProvider.register<WorldStorageService> {
            this.game.lanternPlugin to DefaultWorldStorageService(worldsDirectory)
        }

        val usersDirectory = this.game.gameDirectory.resolve(options.valueOf(LaunchOptions.USERS_DIRECTORY) ?: "users")
        this.userStorageService = this.game.serviceProvider.register<UserStorageService> {
            this.game.lanternPlugin to DefaultUserStorageService(usersDirectory)
        }

        val transportType = TransportType.findBestType()
        val threadFactory = ThreadHelper.newThreadFactory()

        this.nettyBossGroup = transportType.eventLoopGroupSupplier( 0, threadFactory)
        this.nettyWorkerGroup = transportType.eventLoopGroupSupplier( 0, threadFactory)

        this.httpClient = NettyHttpClient("lantern/${this.game.lanternPlugin.version}", this.nettyWorkerGroup)
        val gameProfileService = this.game.serviceProvider.register<GameProfileService> {
            this.game.lanternPlugin to LanternGameProfileService(this.httpClient)
        }

        this.gameProfileManager = LanternGameProfileManager(this.asyncExecutor, gameProfileService)
        this.userManager = LanternUserManager(this, this.userStorageService, this.gameProfileManager)

        this.syncExecutor = mainExecutor.asLanternExecutorService()
        this.syncExecutor.submit { LanternCauseStackManager.setCurrentCauseStack(LanternCauseStack()) }
        this.syncScheduler = LanternScheduler(mainExecutor)

        this.ioExecutor = Dispatchers.IO.asScheduledExecutorService()

        this.worldManager = LanternWorldManager(this, this.ioExecutor, worldStorageService)
        this.worldManager.init()

        this.eventManager.post(LanternStartingServerEvent(this.game, this))

        if (config.server.proxy.type == ProxyType.NONE && !config.server.onlineMode) {
            this.logger.warn("It is not recommend to run the server in offline mode, this allows people to")
            this.logger.warn("choose any username they want. The server will use the account that is attached")
            this.logger.warn("to the username, it doesn't care if it's in offline mode, this will only")
            this.logger.warn("disable the authentication and allow non registered usernames to be used.")
        }

        this.tryBindServer()

        // Start the RCON server, if enabled
        this.game.serviceProvider.register { this.game.lanternPlugin to startRconServer() }

        // Start the Query server, if enabled
        this.queryServer = this.startQueryServer()

        this.loadFavicon()
        this.loadDefaultResourcePack()

        this.syncExecutor.scheduleAtFixedRate({
            try {
                this.update()
            } catch (e: Exception) {
                this.logger.error("Error while updating main loop", e)
            }
        }, 0, 50, TimeUnit.MILLISECONDS)

        this.eventManager.post(LanternStartedServerEvent(this.game, this))

        // Start reading inputs
        this.console.start()

        this.logger.info("Ready for connections.")
    }

    private fun showWelcome() {
        val lanternVersion = this.game.lanternPlugin.version
        val minecraftVersion = this.game.minecraftVersion.name
        val protocolVersion = this.game.minecraftVersion.protocol
        val apiVersion = this.game.spongeApiPlugin.version

        this.logger.info("Starting Lantern $lanternVersion")
        this.logger.info("   for  Minecraft $minecraftVersion with protocol version $protocolVersion")
        this.logger.info("   with SpongeAPI $apiVersion")
    }

    private fun tryBindServer() {
        val config = this.config.server
        val address = this.getBindAddress(config.ip, config.port)

        this.networkManager = NetworkManager(this, this.nettyBossGroup, this.nettyWorkerGroup)

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

        val server = QueryServer(this, config.showPlugins, this.nettyWorkerGroup)
        val address = this.getBindAddress(config.ip, config.port)

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

        val server = RconServer(config.password, this.syncExecutor,
                this.nettyBossGroup, this.nettyWorkerGroup)
        val address = this.getBindAddress(config.ip, config.port)

        this.logger.info("Starting rcon server and binding it to: $address...")
        if (!server.init(address).get()) {
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
                LanternServer::class.java.getResourceAsStream("/data/lantern/icon/favicon.png").use { inputStream ->
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

    private fun update() {
        this.networkManager.pulseSessions()
        this.worldManager.update()
    }

    override fun getBoundAddress(): Optional<InetSocketAddress> = this.networkManager.address.asOptional()

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
        // Kick all the online players
        for (player in this.unsafePlayers)
            player.connection.close(kickMessage)

        // TODO: Wait for players to be disconnected so all events are called

        this.eventManager.post(LanternStoppingServerEvent(this.game, this))

        this.ioExecutor.shutdown()
        this.syncExecutor.shutdown()
        this.networkManager.shutdown()
        this.userManager.shutdown()

        this.queryServer?.shutdown()

        // Stop the async scheduler
        this.asyncExecutor.shutdown()

        for (registration in this.game.serviceProvider.registrations.distinctBy { it.service() }) {
            val service = registration.service()
            if (service is Closeable) {
                try {
                    service.close()
                } catch (ex: Exception) {
                    this.logger.error("A error occurred while closing the ${registration.serviceClass().simpleName}.", ex)
                }
            }
        }

        // Shutdown the game profile manager

        val gameProfileCache = this.gameProfileManager.cache
        if (gameProfileCache is Closeable) {
            try {
                gameProfileCache.close()
            } catch (ex: Exception) {
                this.logger.error("A error occurred while closing the GameProfileCache.", ex)
            }
        }

        // Wait for a while and terminate any rogue threads
        ShutdownMonitorThread(10, TimeUnit.SECONDS).start()
    }

    override fun isDedicatedServer(): Boolean = true
    override fun getGame(): LanternGame = this.game
    override fun getWorldManager(): WorldManager = this.worldManager
    override fun getDefaultResourcePack(): Optional<ResourcePack> = this.defaultResourcePack.asOptional()
    override fun getTeleportHelper(): TeleportHelper = LanternTeleportHelper
    override fun getScheduler(): Scheduler = this.syncScheduler
    override fun getGameProfileManager(): GameProfileManager = this.gameProfileManager
    override fun getUserManager(): UserManager = this.userManager
    override fun getServiceProvider(): LanternServiceProvider = this.game.serviceProvider
    override fun getChunkLayout(): ChunkLayout = LanternChunkLayout

    override fun getMaxPlayers(): Int = this.config.server.maxPlayers
    override fun getMotd(): Text = this.config.server.messageOfTheDay
    override fun getOnlineMode(): Boolean = this.config.server.onlineMode

    override fun hasWhitelist(): Boolean = this.config.server.whitelist
    override fun setHasWhitelist(enabled: Boolean) { this.config.server.whitelist = enabled }

    override fun getPlayerIdleTimeout(): Int = this.config.server.playerIdleTimeout
    override fun setPlayerIdleTimeout(timeout: Int) { this.config.server.playerIdleTimeout = timeout }

    override fun audiences(): Iterable<Audience> = this.audiences
    override fun getBroadcastAudience(): Audience = this.broadcastAudience
    override fun setBroadcastAudience(audience: Audience) { this.broadcastAudience = audience; }

    override fun onMainThread(): Boolean =
            Thread.currentThread() is SyncLanternThread

    override fun getOnlinePlayers(): Collection<Player> = this.playersByUniqueId.values.toImmutableList()

    override fun getTicksPerSecond(): Double = 20.0
    override fun getRunningTimeTicks(): Int = (Duration.between(this.startTime, Instant.now()).toMillis() / 50L).toInt()

    override fun getServerScoreboard(): Optional<Scoreboard> = this.scoreboard.asOptional()
    override fun getCauseStackManager(): CauseStackManager = LanternCauseStackManager

    override fun getPlayer(uniqueId: UUID): Optional<Player> = this.playersByUniqueId[uniqueId].asOptional()
    override fun getPlayer(name: String): Optional<Player> = this.playersByName[name].asOptional()

    /**
     * Adds a [Player] to the online players lookups.
     *
     * @param player The player
     */
    fun addPlayer(player: LanternPlayer) {
        this.playersByName[player.name] = player
        this.playersByUniqueId[player.uniqueId] = player
    }

    /**
     * Removes a [Player] from the online players lookups.
     *
     * @param player The player
     */
    fun removePlayer(player: LanternPlayer) {
        this.playersByName.remove(player.name)
        this.playersByUniqueId.remove(player.uniqueId)
    }
}
