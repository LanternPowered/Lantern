/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.lanternpowered.server.cause.LanternCauseStack;
import org.lanternpowered.server.cause.LanternCauseStackManager;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.console.ConsoleManager;
import org.lanternpowered.server.console.LanternConsoleSource;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.lanternpowered.server.network.NetworkManager;
import org.lanternpowered.server.network.ProxyType;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.query.QueryServer;
import org.lanternpowered.server.network.rcon.RconServer;
import org.lanternpowered.server.network.status.LanternFavicon;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.lanternpowered.server.service.CloseableService;
import org.lanternpowered.server.service.LanternServiceManager;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.util.SecurityHelper;
import org.lanternpowered.server.util.ShutdownMonitorThread;
import org.lanternpowered.server.util.SyncLanternThread;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.lanternpowered.server.world.LanternWorldManager;
import org.lanternpowered.server.world.chunk.LanternChunkLayout;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.network.status.Favicon;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.profile.GameProfileCache;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.resourcepack.ResourcePacks;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.SimpleServiceManager;
import org.spongepowered.api.service.rcon.RconService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.ChunkTicketManager;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.ChunkLayout;
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

@Singleton
public final class LanternServer implements Server {

    // The executor service for the server ticks
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            runnable -> new SyncLanternThread(runnable, "server"));

    // The world manager
    @Inject private LanternWorldManager worldManager;

    // The network manager
    @Inject private NetworkManager networkManager;

    // The logger
    @Inject private Logger logger;

    // The implementation plugin container
    @Inject @Named(InternalPluginsInfo.Implementation.IDENTIFIER) private PluginContainer pluginContainer;

    // The game instance
    @Inject private LanternGame game;

    // The console manager
    @Inject private ConsoleManager consoleManager;

    // The rcon server/service
    @Nullable private RconServer rconServer;

    // The query server
    @Nullable private QueryServer queryServer;

    // The key pair used for authentication
    private final KeyPair keyPair = SecurityHelper.generateKeyPair();

    // The broadcast channel
    private volatile MessageChannel broadcastChannel = MessageChannel.TO_ALL;

    // The maximum amount of players that can join
    private int maxPlayers;

    // The amount of ticks the server is running
    private final AtomicInteger runningTimeTicks = new AtomicInteger(0);

    // All the players by their name
    private final Map<String, LanternPlayer> playersByName = new ConcurrentHashMap<>();

    // A unmodifiable collection with all the players
    private final Collection<LanternPlayer> unmodifiablePlayers = Collections.unmodifiableCollection(this.playersByName.values());

    // All the players by their uniqueId
    private final Map<UUID, LanternPlayer> playersByUUID = new ConcurrentHashMap<>();

    @Nullable private ResourcePack resourcePack;
    @Nullable private Favicon favicon;
    private boolean onlineMode;
    private boolean whitelist;

    private volatile boolean shuttingDown;

    @Inject
    private LanternServer() {
    }

    /**
     * Gets the {@link LanternGame} instance.
     *
     * @return The game
     */
    public LanternGame getGame() {
        return this.game;
    }

    void initialize() {
        // First initialize the console manager, but don't start to read anything yet
        this.consoleManager.init();

        this.logger.info("Starting Lantern Server {}",
                firstNonNull(InternalPluginsInfo.Implementation.VERSION, ""));
        this.logger.info("   for  Minecraft {} with protocol version {}",
                LanternMinecraftVersion.CURRENT.getName(),
                LanternMinecraftVersion.CURRENT.getProtocol());
        this.logger.info("   with SpongeAPI {}",
                firstNonNull(InternalPluginsInfo.Api.VERSION, ""));

        try {
            this.game.initialize();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to Pre Initialize the Game.", e);
        }
    }

    void start() throws IOException {
        final GlobalConfig globalConfig = this.game.getGlobalConfig();
        // Enable the query server if needed
        if (globalConfig.isQueryEnabled()) {
            this.queryServer = new QueryServer(this.game, globalConfig.getShowPluginsToQuery());
        }
        // Enable the rcon server if needed
        if (globalConfig.isRconEnabled()) {
            this.rconServer = new RconServer(globalConfig.getRconPassword());
            this.game.getServiceManager().setProvider(this.pluginContainer, RconService.class, this.rconServer);
        }
        if (globalConfig.getProxyType() == ProxyType.NONE && !globalConfig.isOnlineMode()) {
            this.logger.warn("It is not recommend to run the server in offline mode, this allows people to");
            this.logger.warn("choose any username they want. The server will use the account that is attached");
            this.logger.warn("to the username, it doesn't care if it's in offline mode, this will only");
            this.logger.warn("disable the authentication and allow non registered usernames to be used.");
        }

        this.consoleManager.start();

        try {
            bind();
        } catch (BindException e) {
            // descriptive bind error messages
            this.logger.error("The server could not bind to the requested address.");
            if (e.getMessage().startsWith("Cannot assign requested address")) {
                this.logger.error("The 'server.ip' in your global.conf file may not be valid.");
                this.logger.error("Unless you are sure you need it, try removing it.");
                this.logger.error(e.toString());
            } else if (e.getMessage().startsWith("Address already in use")) {
                this.logger.error("The address was already in use. Check that no server is");
                this.logger.error("already running on that port. If needed, try killing all");
                this.logger.error("Java processes using Task Manager or similar.");
                this.logger.error(e.toString());
            } else {
                this.logger.error("An unknown bind error has occurred.", e);
            }
            System.exit(1);
            return;
        }
        bindQuery();
        bindRcon();

        this.logger.info("Ready for connections.");
        this.worldManager.init();

        final Cause gameCause = Cause.of(EventContext.empty(), this.game);

        this.game.postGameStateChange(SpongeEventFactory.createGameAboutToStartServerEvent(gameCause));
        this.game.postGameStateChange(SpongeEventFactory.createGameStartingServerEvent(gameCause));

        final GlobalConfig config = this.game.getGlobalConfig();
        this.maxPlayers = config.getMaxPlayers();
        this.onlineMode = config.isOnlineMode();

        final Path faviconPath = Paths.get(config.getFavicon());
        if (Files.exists(faviconPath)) {
            try {
                this.favicon = LanternFavicon.load(faviconPath);
            } catch (IOException e) {
                this.logger.error("Failed to load the favicon", e);
            }
        } else {
            try {
                this.favicon = LanternFavicon.load(getGame().getAssetManager().getAsset(
                        InternalPluginsInfo.Implementation.IDENTIFIER, "icon/favicon.png").get().getUrl());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load the default favicon.");
            }
        }

        final String resourcePackPath = config.getDefaultResourcePack();
        if (!resourcePackPath.isEmpty()) {
            try {
                this.resourcePack = ResourcePacks.fromUri(URI.create(resourcePackPath));
            } catch (FileNotFoundException e) {
                this.logger.warn("Couldn't find a valid resource pack at the location: {}", resourcePackPath, e);
            }
        }

        // Initialize a CauseStack on the server thread.
        this.executor.submit(() -> LanternCauseStackManager.INSTANCE.setCurrentCauseStack(new LanternCauseStack()));
        // Start server ticking.
        this.executor.scheduleAtFixedRate(() -> {
            try {
                pulse();
            } catch (Exception e) {
                this.logger.error("Error while pulsing", e);
            }
        }, 0, LanternGame.TICK_DURATION, TimeUnit.MILLISECONDS);

        this.game.postGameStateChange(SpongeEventFactory.createGameStartedServerEvent(gameCause));
    }

    /**
     * Get the socket address to bind to for a specified service.
     * 
     * @param port the port to use
     * @return the socket address
     */
    private InetSocketAddress getBindAddress(int port) {
        final String ip = this.game.getGlobalConfig().getServerIp();
        if (ip.length() == 0) {
            return new InetSocketAddress(port);
        } else {
            return new InetSocketAddress(ip, port);
        }
    }

    private void bind() throws BindException {
        final InetSocketAddress address = this.getBindAddress(this.game.getGlobalConfig().getServerPort());
        final GlobalConfig.NetworkTransport networkTransport = this.game.getGlobalConfig().getServerNetworkTransport();

        ProtocolState.init();
        final ChannelFuture future = this.networkManager.init(address,
                networkTransport.allowsEpoll(), networkTransport.allowsKQueue());
        final Channel channel = future.awaitUninterruptibly().channel();
        if (!channel.isActive()) {
            final Throwable cause = future.cause();
            if (cause instanceof BindException) {
                throw (BindException) cause;
            }
            throw new RuntimeException("Failed to bind to address", cause);
        }

        this.logger.info("Successfully bound to: " + channel.localAddress());
    }

    private void bindQuery() {
        if (this.queryServer == null) {
            return;
        }

        final InetSocketAddress address = getBindAddress(this.game.getGlobalConfig().getQueryPort());
        final GlobalConfig.NetworkTransport networkTransport = this.game.getGlobalConfig().getQueryNetworkTransport();
        this.game.getLogger().info("Binding query to address: " + address + "...");

        final ChannelFuture future = this.queryServer.init(address,
                networkTransport.allowsEpoll(), networkTransport.allowsKQueue());
        final Channel channel = future.awaitUninterruptibly().channel();
        if (!channel.isActive()) {
            this.game.getLogger().warn("Failed to bind query. Address already in use?");
        }
    }

    private void bindRcon() {
        if (this.rconServer == null) {
            return;
        }

        final InetSocketAddress address = this.getBindAddress(this.game.getGlobalConfig().getRconPort());
        final GlobalConfig.NetworkTransport networkTransport = this.game.getGlobalConfig().getRconNetworkTransport();
        this.game.getLogger().info("Binding rcon to address: " + address + "...");

        final ChannelFuture future = this.rconServer.init(address,
                networkTransport.allowsEpoll(), networkTransport.allowsKQueue());
        final Channel channel = future.awaitUninterruptibly().channel();
        if (!channel.isActive()) {
            this.game.getLogger().warn("Failed to bind rcon. Address already in use?");
        }
    }

    /**
     * Pulses (ticks) the game.
     */
    private void pulse() {
        this.runningTimeTicks.incrementAndGet();
        // Pulse the network sessions
        this.networkManager.pulseSessions();
        // Pulse the sync scheduler tasks
        this.game.getScheduler().pulseSyncScheduler();
        // Pulse the world threads
        this.worldManager.pulse();
    }

    /**
     * Gets the key pair.
     * 
     * @return the key pair
     */
    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    /**
     * Gets the favicon of the server.
     * 
     * @return the favicon
     */
    public Optional<Favicon> getFavicon() {
        return Optional.ofNullable(this.favicon);
    }

    /**
     * Adds a {@link Player} to the online players lookups.
     *
     * @param player The player
     */
    public void addPlayer(LanternPlayer player) {
        this.playersByName.put(player.getName(), player);
        this.playersByUUID.put(player.getUniqueId(), player);
    }

    /**
     * Removes a {@link Player} from the online players lookups.
     *
     * @param player The player
     */
    public void removePlayer(LanternPlayer player) {
        this.playersByName.remove(player.getName());
        this.playersByUUID.remove(player.getUniqueId());
    }

    /**
     * Gets a raw collection with all the players.
     *
     * @return The players
     */
    public Collection<LanternPlayer> getRawOnlinePlayers() {
        return this.unmodifiablePlayers;
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return ImmutableList.copyOf(this.playersByName.values());
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        return Optional.ofNullable(this.playersByUUID.get(checkNotNull(uniqueId, "uniqueId")));
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        return Optional.ofNullable(this.playersByName.get(checkNotNull(name, "name")));
    }

    @Override
    public Collection<World> getWorlds() {
        return this.worldManager.getWorlds();
    }

    @Override
    public Collection<WorldProperties> getUnloadedWorlds() {
        return this.worldManager.getUnloadedWorlds();
    }

    @Override
    public Collection<WorldProperties> getAllWorldProperties() {
        return this.worldManager.getAllWorldProperties();
    }

    @Override
    public Optional<World> getWorld(UUID uniqueId) {
        return this.worldManager.getWorld(uniqueId);
    }

    @Override
    public Optional<World> getWorld(String worldName) {
        return this.worldManager.getWorld(worldName);
    }

    @Override
    public Optional<WorldProperties> getDefaultWorld() {
        return this.worldManager.getDefaultWorld();
    }

    @Override
    public String getDefaultWorldName() {
        return this.game.getGlobalConfig().getRootWorldFolder();
    }

    @Override
    public Optional<World> loadWorld(String worldName) {
        return this.worldManager.loadWorld(worldName);
    }

    @Override
    public Optional<World> loadWorld(UUID uniqueId) {
        return this.worldManager.loadWorld(uniqueId);
    }

    @Override
    public Optional<World> loadWorld(WorldProperties properties) {
        return this.worldManager.loadWorld(properties);
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(String worldName) {
        return this.worldManager.getWorldProperties(worldName);
    }

    @Override
    public Optional<WorldProperties> getWorldProperties(UUID uniqueId) {
        return this.worldManager.getWorldProperties(uniqueId);
    }

    @Override
    public boolean unloadWorld(World world) {
        return this.worldManager.unloadWorld(world);
    }

    @Override
    public WorldProperties createWorldProperties(String folderName, WorldArchetype worldArchetype) throws IOException {
        return this.worldManager.createWorldProperties(folderName, worldArchetype);
    }

    @Override
    public CompletableFuture<Optional<WorldProperties>> copyWorld(WorldProperties worldProperties, String copyName) {
        return this.worldManager.copyWorld(worldProperties, copyName);
    }

    @Override
    public Optional<WorldProperties> renameWorld(WorldProperties worldProperties, String newName) {
        return this.worldManager.renameWorld(worldProperties, newName);
    }

    @Override
    public CompletableFuture<Boolean> deleteWorld(WorldProperties worldProperties) {
        return this.worldManager.deleteWorld(worldProperties);
    }

    @Override
    public boolean saveWorldProperties(WorldProperties properties) {
        return this.worldManager.saveWorldProperties(properties);
    }

    @Override
    public Optional<Scoreboard> getServerScoreboard() {
        return Optional.empty();
    }

    @Override
    public ChunkLayout getChunkLayout() {
        return LanternChunkLayout.INSTANCE;
    }

    @Override
    public int getRunningTimeTicks() {
        return this.runningTimeTicks.get();
    }

    @Override
    public MessageChannel getBroadcastChannel() {
        return this.broadcastChannel;
    }

    @Override
    public void setBroadcastChannel(MessageChannel channel) {
        this.broadcastChannel = checkNotNull(channel, "channel");
    }

    @Override
    public Optional<InetSocketAddress> getBoundAddress() {
        return this.networkManager.getAddress().filter(a -> a instanceof InetSocketAddress).map(a -> (InetSocketAddress) a);
    }

    @Override
    public boolean hasWhitelist() {
        return this.whitelist;
    }

    @Override
    public void setHasWhitelist(boolean enabled) {
        this.whitelist = enabled;
    }

    @Override
    public boolean getOnlineMode() {
        return this.onlineMode;
    }

    @Override
    public Text getMotd() {
        return this.game.getGlobalConfig().getMotd();
    }

    @Override
    public void shutdown() {
        shutdown(this.game.getGlobalConfig().getShutdownMessage());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void shutdown(Text kickMessage) {
        checkNotNull(kickMessage, "kickMessage");
        if (this.shuttingDown) {
            return;
        }
        this.shuttingDown = true;

        // Stop the console
        this.consoleManager.shutdown();

        final Cause gameCause = Cause.of(EventContext.empty(), this.game);
        this.game.postGameStateChange(SpongeEventFactory.createGameStoppingServerEvent(gameCause));

        // Debug a message
        this.logger.info("Stopping the server... ({})", LanternTexts.toLegacy(kickMessage));

        // Kick all the online players
        getOnlinePlayers().forEach(player -> ((LanternPlayer) player).getConnection().disconnect(kickMessage));

        // Stop the network servers - starts the shutdown process
        // It may take a second or two for Netty to totally clean up
        this.networkManager.shutdown();

        if (this.queryServer != null) {
            this.queryServer.shutdown();
        }
        if (this.rconServer != null) {
            this.rconServer.shutdown();
        }

        // Stop the world manager
        this.worldManager.shutdown();

        // Shutdown the executor
        this.executor.shutdown();

        // Stop the async scheduler
        this.game.getScheduler().shutdownAsyncScheduler(10, TimeUnit.SECONDS);

        final Collection<ProviderRegistration<?>> serviceRegistrations;
        try {
            final ServiceManager serviceManager = this.game.getServiceManager();
            checkState(serviceManager instanceof SimpleServiceManager || serviceManager instanceof LanternServiceManager);

            final Field field = (serviceManager instanceof SimpleServiceManager ? SimpleServiceManager.class :
                    LanternServiceManager.class).getDeclaredField("providers");
            field.setAccessible(true);

            //noinspection unchecked
            final Map<Class<?>, ProviderRegistration<?>> map = (Map<Class<?>, ProviderRegistration<?>>) field.get(serviceManager);
            serviceRegistrations = map.values();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw UncheckedThrowables.throwUnchecked(e);
        }

        // Close all the services if possible
        serviceRegistrations.forEach(provider -> {
            final Object service = provider.getProvider();
            if (service instanceof CloseableService) {
                try {
                    ((CloseableService) service).close();
                } catch (Exception e) {
                    this.logger.error("A error occurred while closing the {}.", provider.getService().getName(), e);
                }
            }
        });

        // Shutdown the game profile manager
        this.game.getGameProfileManager().getDefaultCache().save();
        final GameProfileCache cache = this.game.getGameProfileManager().getCache();
        if (cache instanceof CloseableService) {
            try {
                ((CloseableService) cache).close();
            } catch (Exception e) {
                this.logger.error("A error occurred while closing the GameProfileCache.", e);
            }
        }

        try {
            this.game.getOpsConfig().save();
        } catch (IOException e) {
            this.logger.error("A error occurred while saving the ops config.", e);
        }

        this.game.postGameStateChange(SpongeEventFactory.createGameStoppedServerEvent(gameCause));
        this.game.postGameStateChange(SpongeEventFactory.createGameStoppingEvent(gameCause));
        this.game.postGameStateChange(SpongeEventFactory.createGameStoppedEvent(gameCause));

        // Wait for a while and terminate any rogue threads
        new ShutdownMonitorThread(10, TimeUnit.SECONDS).start();
    }

    @Override
    public ConsoleSource getConsole() {
        return LanternConsoleSource.INSTANCE;
    }

    @Override
    public ChunkTicketManager getChunkTicketManager() {
        return this.game.getChunkTicketManager();
    }

    @Override
    public double getTicksPerSecond() {
        return LanternGame.TICKS_PER_SECOND; // TODO
    }

    @Override
    public Optional<ResourcePack> getDefaultResourcePack() {
        return Optional.ofNullable(this.resourcePack);
    }

    @Override
    public int getPlayerIdleTimeout() {
        return this.game.getGlobalConfig().getPlayerIdleTimeout();
    }

    @Override
    public void setPlayerIdleTimeout(int timeout) {
        this.game.getGlobalConfig().setPlayerIdleTimeout(timeout);
    }

    @Override
    public boolean isMainThread() {
        return Thread.currentThread() instanceof SyncLanternThread;
    }

    @Override
    public GameProfileManager getGameProfileManager() {
        return this.game.getGameProfileManager();
    }

    /**
     * Gets the {@link LanternWorldManager}.
     *
     * @return The world manager
     */
    public LanternWorldManager getWorldManager() {
        return this.worldManager;
    }

}
