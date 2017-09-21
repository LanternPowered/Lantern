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
package org.lanternpowered.server.game;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.lanternpowered.launch.Environment;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.asset.LanternAssetManager;
import org.lanternpowered.server.command.DefaultCommandsCollection;
import org.lanternpowered.server.command.LanternCommandManager;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.config.user.OpsConfig;
import org.lanternpowered.server.config.user.OpsEntry;
import org.lanternpowered.server.config.user.UserConfig;
import org.lanternpowered.server.config.user.WhitelistConfig;
import org.lanternpowered.server.config.user.ban.BanConfig;
import org.lanternpowered.server.data.LanternDataManager;
import org.lanternpowered.server.data.property.LanternPropertyRegistry;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.event.LanternCauseStack;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.lanternpowered.server.game.version.MinecraftVersionCache;
import org.lanternpowered.server.inject.Option;
import org.lanternpowered.server.inject.Service;
import org.lanternpowered.server.inject.ServiceProvider;
import org.lanternpowered.server.network.channel.LanternChannelRegistrar;
import org.lanternpowered.server.network.protocol.Protocol;
import org.lanternpowered.server.network.rcon.EmptyRconService;
import org.lanternpowered.server.permission.Permissions;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.lanternpowered.server.profile.LanternGameProfileManager;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.service.LanternServiceListeners;
import org.lanternpowered.server.service.pagination.LanternPaginationService;
import org.lanternpowered.server.service.permission.LanternContextCalculator;
import org.lanternpowered.server.service.permission.LanternPermissionService;
import org.lanternpowered.server.service.sql.LanternSqlService;
import org.lanternpowered.server.service.user.LanternUserStorageService;
import org.lanternpowered.server.world.chunk.LanternChunkTicketManager;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameState;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameStateEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.SynchronousExecutor;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.rcon.RconService;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.TeleportHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

@Singleton
public class LanternGame implements Game {

    // The singleton instance of the game
    static LanternGame game;

    // The server will aim for 20 ticks per second
    public static final int TICKS_PER_SECOND = 20;

    // The amount of milli seconds in one tick
    public static final int TICK_DURATION = 1000 / TICKS_PER_SECOND;

    // The amount of nano seconds in one tick
    public static final long TICK_DURATION_NS = TimeUnit.NANOSECONDS.convert(TICK_DURATION, TimeUnit.MILLISECONDS);

    /**
     * Gets the current time in ticks. This method is similar to
     * {@link System#currentTimeMillis()} but the unit is converted
     * to ticks.
     * 
     * @return The current time in ticks
     */
    public static long currentTimeTicks() {
        return System.currentTimeMillis() / TICK_DURATION;
    }

    @Inject private Logger logger;
    @Inject private org.apache.logging.log4j.Logger log4jLogger;

    // The platform
    @Inject private LanternPlatform platform;

    // The channel registrar
    @Inject private LanternChannelRegistrar channelRegistrar;

    // The server
    @Inject private LanternServer server;

    // The game profile manager
    @Inject private LanternGameProfileManager gameProfileManager;

    // The plugin manager
    @Inject private LanternPluginManager pluginManager;

    // The event manager
    @Inject private EventManager eventManager;

    // The service manager
    @Inject private ServiceManager serviceManager;

    // The game registry
    @Inject private LanternGameRegistry gameRegistry;

    // The scheduler
    @Inject private LanternScheduler scheduler;

    // The sync scheduler service
    @Inject @SynchronousExecutor private SpongeExecutorService syncExecutorService;

    // The chunk load service
    @Inject private LanternChunkTicketManager chunkTicketManager;

    // The command manager
    @Inject private LanternCommandManager commandManager;

    // The asset manager
    @Inject private LanternAssetManager assetManager;

    // The config manager
    @Inject private ConfigManager configManager;

    // The teleport helper
    @Inject private TeleportHelper teleportHelper;

    // The data manager
    @Inject private LanternDataManager dataManager;

    // The property registry
    @Inject private LanternPropertyRegistry propertyRegistry;

    // The inbuilt plugin containers
    @Inject @Named(InternalPluginsInfo.Api.IDENTIFIER) private PluginContainer apiContainer;
    @Inject @Named(InternalPluginsInfo.Minecraft.IDENTIFIER) private PluginContainer minecraft;
    @Inject @Named(InternalPluginsInfo.SpongePlatform.IDENTIFIER) private PluginContainer spongePlatformContainer;
    @Inject @Named(InternalPluginsInfo.Implementation.IDENTIFIER) private PluginContainer implContainer;

    // The game folder
    @Inject @Named(DirectoryKeys.ROOT) private Path gameFolder;
    // The folder where the worlds are saved
    @Inject @Named(DirectoryKeys.ROOT_WORLD) private Provider<Path> rootWorldFolder;

    // The global config
    @Inject private GlobalConfig globalConfig;
    // The ops config
    @Inject private OpsConfig opsConfig;

    /// Services

    // The Whitelist Service
    @Inject @ServiceProvider(WhitelistConfig.class) private Service<WhitelistService> whitelistService;
    // The Ban Service
    @Inject @ServiceProvider(BanConfig.class) private Service<BanService> banService;
    // The User Storage Service
    @Inject @ServiceProvider(LanternUserStorageService.class) private Service<UserStorageService> userStorageService;
    // The Pagination Service
    @Inject @ServiceProvider(LanternPaginationService.class) private Service<PaginationService> paginationService;
    // The SQL Service
    @Inject @ServiceProvider(LanternSqlService.class) private Service<SqlService> sqlService;
    // The Permission Service
    @Inject @ServiceProvider(LanternPermissionService.class) private Service<PermissionService> permissionService;

    // The minecraft version cache
    @Inject private MinecraftVersionCache minecraftVersionCache;

    // The injector
    @Inject private Injector injector;

    @Inject @Option("scanClasspath") @Nullable private Boolean scanClasspath;

    // The current game state
    @Nullable private GameState gameState = null;

    @Inject
    private LanternGame() {
        game = this;

        // Set the CauseStack for the main thread, at startup
        CauseStack.set(new LanternCauseStack());
    }

    public void initialize() throws IOException {
        final LanternMinecraftVersion versionCacheEntry = this.minecraftVersionCache.getVersionOrUnknown(
                Protocol.CURRENT_VERSION, false);
        if (!LanternMinecraftVersion.CURRENT.equals(versionCacheEntry)) {
            throw new RuntimeException("The current version and version in the cache don't match: " +
                    LanternMinecraftVersion.CURRENT + " != " + versionCacheEntry);
        }

        // Load the plugin instances
        try {
            // By default, use the '--scanClasspath <true|false>' option, if it can't
            // be found, fall back to a environment based decision
            Boolean scanClasspath = this.scanClasspath;
            if (scanClasspath == null) {
                scanClasspath = Environment.get() == Environment.DEVELOPMENT;
            }
            this.pluginManager.loadPlugins(scanClasspath);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while loading the plugins.", e);
        }

        this.gameRegistry.registerDefaults();
        this.gameRegistry.earlyRegistry();

        // Load the global configuration
        this.globalConfig.load();
        // Save missing settings
        this.globalConfig.save();

        // They should not be replaced by now
        this.whitelistService.extended(WhitelistConfig.class).get().load();
        this.banService.extended(BanConfig.class).get().load();

        // Create the event manager instance
        this.eventManager.registerListeners(this.implContainer, LanternServiceListeners.getInstance());
        this.pluginManager.registerPluginInstances();

        // Call pre registry phase.
        this.gameRegistry.preRegistry();

        // Register temporarily a empty rcon service
        registerService(RconService.class, new EmptyRconService(this.globalConfig.getRconPassword()));

        // Create the cause to post events...
        final CauseStack causeStack = CauseStack.current();
        causeStack.pushCause(this);
        final Cause gameCause = causeStack.getCurrentCause();

        // Call the construction events
        postGameStateChange(SpongeEventFactory.createGameConstructionEvent(gameCause));

        // Call pre init phase for registry
        this.gameRegistry.preInit();

        LanternServiceListeners.getInstance().registerServiceCallback(PermissionService.class,
                input -> {
                    this.server.getConsole().getContainingCollection();
                    input.registerContextCalculator(new LanternContextCalculator());
                });

        // Pre-init phase
        postGameStateChange(SpongeEventFactory.createGamePreInitializationEvent(gameCause));

        // Call init phase for registry
        this.gameRegistry.init();

        final PermissionService permissionService = this.permissionService.get();
        if (permissionService instanceof LanternPermissionService) {
            final LanternPermissionService service = (LanternPermissionService) permissionService;

            service.getGroupForOpLevel(Permissions.SELECTOR_LEVEL).getSubjectData()
                    .setPermission(SubjectData.GLOBAL_CONTEXT, Permissions.SELECTOR_PERMISSION, Tristate.TRUE);
            service.getGroupForOpLevel(Permissions.COMMAND_BLOCK_LEVEL).getSubjectData()
                    .setPermission(SubjectData.GLOBAL_CONTEXT, Permissions.COMMAND_BLOCK_PERMISSION, Tristate.TRUE);
            service.getGroupForOpLevel(Permissions.Login.BYPASS_PLAYER_LIMIT_LEVEL).getSubjectData()
                    .setPermission(SubjectData.GLOBAL_CONTEXT, Permissions.Login.BYPASS_PLAYER_LIMIT_PERMISSION, Tristate.FALSE);
            service.getGroupForOpLevel(Permissions.Login.BYPASS_WHITELIST_LEVEL).getSubjectData()
                    .setPermission(SubjectData.GLOBAL_CONTEXT, Permissions.Login.BYPASS_WHITELIST_PERMISSION, Tristate.TRUE);
            service.getGroupForOpLevel(Permissions.Chat.FORMAT_URLS_LEVEL).getSubjectData()
                    .setPermission(SubjectData.GLOBAL_CONTEXT, Permissions.Chat.FORMAT_URLS, Tristate.TRUE);
        }

        // Load the default commands
        this.injector.getInstance(DefaultCommandsCollection.class).load();

        // Init phase
        postGameStateChange(SpongeEventFactory.createGameInitializationEvent(gameCause));

        // Call post init phase for registry
        this.gameRegistry.postInit();

        // Post-init phase
        postGameStateChange(SpongeEventFactory.createGamePostInitializationEvent(gameCause));

        // Load-complete phase
        postGameStateChange(SpongeEventFactory.createGameLoadCompleteEvent(gameCause));

        // Pop off the game instance
        causeStack.popCause();
    }

    public <T extends GameStateEvent> void postGameStateChange(T event) {
        checkNotNull(event, "gameState");
        final GameState[] gameStates = GameState.values();
        final int current = this.gameState == null ? -1 : this.gameState.ordinal();
        checkArgument(current < gameStates.length - 1,
                "The game already reached the last state, but got %s", event.getState().name());
        checkArgument(current == event.getState().ordinal() - 1,
                "Expected for the next state %s, but got %s",
                gameStates[current + 1].name(), event.getState().name());
        this.gameState = event.getState();
        this.eventManager.post(event);
    }

    private <T> void registerService(Class<T> serviceClass, T serviceImpl) {
        this.serviceManager.setProvider(this.minecraft, serviceClass, serviceImpl);
    }

    /**
     * Gets the plugin container that represents the minecraft server.
     * 
     * @return The plugin container
     */
    public PluginContainer getMinecraftPlugin() {
        return this.minecraft;
    }

    /**
     * Gets the plugin container that represents the implementation.
     *
     * @return The plugin container
     */
    public PluginContainer getImplementationPlugin() {
        return this.implContainer;
    }

    /**
     * Gets the plugin container that represents the api.
     *
     * @return The plugin container
     */
    public PluginContainer getApiPlugin() {
        return this.apiContainer;
    }

    /**
     * Gets the plugin container that represents the sponge platform.
     *
     * @return The plugin container
     */
    public PluginContainer getSpongePlugin() {
        return this.spongePlatformContainer;
    }

    /**
     * Gets the {@link GlobalConfig}.
     * 
     * @return The global configuration
     */
    public GlobalConfig getGlobalConfig() {
        return this.globalConfig;
    }

    /**
     * Gets the {@link OpsConfig}.
     * 
     * @return The ops configuration
     */
    public UserConfig<OpsEntry> getOpsConfig() {
        return this.opsConfig;
    }

    @Override
    public GameState getState() {
        final GameState gameState = this.gameState;
        checkState(gameState != null, "The game hasn't reached the construction state");
        return gameState;
    }

    /**
     * Gets the logger of the game.
     * 
     * @return The logger
     */
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * Gets the logger of the game.
     *
     * @return The logger
     */
    public org.apache.logging.log4j.Logger getLog4jLogger() {
        return this.log4jLogger;
    }

    @Override
    public LanternServer getServer() {
        return this.server;
    }

    @Override
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public LanternAssetManager getAssetManager() {
        return this.assetManager;
    }

    @Override
    public LanternGameRegistry getRegistry() {
        return this.gameRegistry;
    }

    @Override
    public Optional<GameDictionary> getGameDictionary() {
        return Optional.empty();
    }

    @Override
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public TeleportHelper getTeleportHelper() {
        return this.teleportHelper;
    }

    @Override
    public LanternPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public boolean isServerAvailable() {
        return true;
    }

    @Override
    public LanternScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public Path getGameDirectory() {
        return this.gameFolder;
    }

    @Override
    public Path getSavesDirectory() {
        return this.rootWorldFolder.get();
    }

    @Override
    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public LanternPropertyRegistry getPropertyRegistry() {
        return this.propertyRegistry;
    }

    @Override
    public LanternDataManager getDataManager() {
        return this.dataManager;
    }

    /**
     * Gets the {@link LanternChunkTicketManager}.
     *
     * @return The chunk ticket manager
     */
    public LanternChunkTicketManager getChunkTicketManager() {
        return this.chunkTicketManager;
    }

    /**
     * Gets the {@link LanternChannelRegistrar}.
     * 
     * @return The channel registrar
     */
    public LanternChannelRegistrar getChannelRegistrar() {
        return this.channelRegistrar;
    }

    /**
     * Gets the {@link LanternGameProfileManager}.
     * 
     * @return The game profile manager
     */
    public LanternGameProfileManager getGameProfileManager() {
        return this.gameProfileManager;
    }

    public SpongeExecutorService getSyncExecutorService() {
        return this.syncExecutorService;
    }

    public MinecraftVersionCache getMinecraftVersionCache() {
        return this.minecraftVersionCache;
    }

    public UserStorageService getUserStorageService() {
        return this.userStorageService.get();
    }
}
