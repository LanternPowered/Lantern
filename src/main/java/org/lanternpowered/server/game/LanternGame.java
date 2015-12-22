/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.command.CommandHelp;
import org.lanternpowered.server.command.CommandStop;
import org.lanternpowered.server.command.CommandVersion;
import org.lanternpowered.server.command.LanternCommandDisambiguator;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.config.LanternConfigManager;
import org.lanternpowered.server.config.user.UserConfig;
import org.lanternpowered.server.config.user.OpsEntry;
import org.lanternpowered.server.config.user.WhitelistConfig;
import org.lanternpowered.server.config.user.ban.BanConfig;
import org.lanternpowered.server.data.LanternDataManager;
import org.lanternpowered.server.event.LanternEventManager;
import org.lanternpowered.server.network.channel.LanternChannelRegistrar;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.lanternpowered.server.plugin.LanternServerContainer;
import org.lanternpowered.server.plugin.MinecraftPluginContainer;
import org.lanternpowered.server.plugin.SpongeApiContainer;
import org.lanternpowered.server.profile.LanternGameProfileManager;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.service.pagination.LanternPaginationService;
import org.lanternpowered.server.service.permission.LanternPermissionService;
import org.lanternpowered.server.service.sql.LanternSqlService;
import org.lanternpowered.server.service.user.LanternUserStorageService;
import org.lanternpowered.server.text.action.LanternCallbackHolder;
import org.lanternpowered.server.util.ReflectionHelper;
import org.lanternpowered.server.world.LanternTeleportHelper;
import org.lanternpowered.server.world.chunk.LanternChunkTicketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.property.PropertyRegistry;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.SimpleServiceManager;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.SimpleCommandManager;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.TeleportHelper;

public class LanternGame implements Game {

    public static final String API_NAME = "SpongeAPI";
    public static final String API_ID = "spongeapi";
    public static final String API_VERSION = "DEV";

    public static final String IMPL_NAME = "LanternServer";
    public static final String IMPL_ID = "lanternserver";
    public static final String IMPL_VERSION = "DEV";

    public static final String MINECRAFT_ID = "minecraft";
    public static final String MINECRAFT_NAME = "Minecraft";
    public static final String MINECRAFT_VERSION = "1.8.9";

    // The name of the config folder
    public static final String CONFIG_FOLDER = "config";

    // The name of the global config file
    public static final String GLOBAL_CONFIG = "global.conf";

    // The name of the ops config file
    public static final String OPS_CONFIG = "ops.conf";

    // The name of the whitelist config file
    public static final String WHITELIST_CONFIG = "whitelist.conf";

    // The name of the ban config file
    public static final String BANS_CONFIG = "bans.conf";

    // The name of the config folder
    public static final String PLUGINS_FOLDER = "plugins";

    // The singleton instance of the game
    private static LanternGame game;

    // The server will aim for 20 ticks per second
    public static final int TICKS_PER_SECOND = 20;

    // The amount of milli seconds in one tick
    public static final int TICK_DURATION = 1000 / TICKS_PER_SECOND;

    // The amount of nano seconds in one tick
    public static final long TICK_DURATION_NS = TimeUnit.NANOSECONDS.convert(TICK_DURATION, TimeUnit.MILLISECONDS);

    // The logger of the game
    private static Logger logger;

    /**
     * Gets the current time in ticks. This method is similar to
     * {@link System#currentTimeMillis()} but the unit is converted
     * to ticks.
     * 
     * @return the current time in ticks
     */
    public static long currentTimeTicks() {
        return System.currentTimeMillis() / TICK_DURATION;
    }

    /**
     * Gets the instance of the game.
     * 
     * @return the instance
     */
    public static LanternGame get() {
        return game;
    }

    /**
     * Gets the logger of the game.
     * 
     * @return the logger
     */
    public static Logger log() {
        if (logger == null) {
            logger = LoggerFactory.getLogger("");
        }
        return logger;
    }

    /**
     * Gets the internal plugin of the game.
     * 
     * @return the plugin
     */
    public static PluginContainer plugin() {
        return game.minecraft;
    }

    // The platform
    private LanternPlatform platform;

    // The config folder
    private Path configFolder;

    // The plugins folder
    private Path pluginsFolder;

    // The channel registrar
    private LanternChannelRegistrar channelRegistrar;

    // The server
    private LanternServer server;

    // The game profile manager
    private LanternGameProfileManager gameProfileManager;

    // The plugin manager
    private LanternPluginManager pluginManager;

    // The event manager
    private LanternEventManager eventManager;

    // The service manager
    private ServiceManager serviceManager;

    // The game registry
    private LanternGameRegistry gameRegistry;

    // The game dictionary
    private LanternGameDictionary gameDictionary;

    // The scheduler
    private LanternScheduler scheduler;

    // The chunk load service
    private LanternChunkTicketManager chunkTicketManager;

    // The serialization service 
    private LanternDataManager dataManager;

    // The config manager
    private ConfigManager configManager;

    // The teleport helper
    private TeleportHelper teleportHelper;

    // The inbuilt plugin containers
    private PluginContainer minecraft;
    private PluginContainer apiContainer;
    private PluginContainer implContainer;

    // The folder where the worlds are saved
    private Path rootWorldFolder;

    // The global config
    private GlobalConfig globalConfig;
    // The ops config
    private UserConfig<OpsEntry> opsConfig;
    // The whitelist config
    private WhitelistConfig whitelistConfig;
    // The ban config
    private BanConfig banConfig;

    // The current game state
    private GameState gameState = GameState.CONSTRUCTION;

    public LanternGame() {
        if (game != null) {
            throw new IllegalStateException("The game can only be initialized once!");
        }
        game = this;
        try {
            ReflectionHelper.setField(Sponge.class.getDeclaredField("game"), null, this);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while setting the game instance for the Sponge class.", e);
        }
    }

    public void preInitialize() throws IOException {
        final Path root = new File("").toPath();
        this.configFolder = root.resolve(CONFIG_FOLDER);
        this.pluginsFolder = root.resolve(PLUGINS_FOLDER);

        // Create the inbuilt plugin containers and platform
        this.minecraft = new MinecraftPluginContainer(this);
        this.apiContainer = new SpongeApiContainer();
        this.implContainer = new LanternServerContainer();
        this.platform = new LanternPlatform(this.apiContainer, this.implContainer);

        // Pre register some game objects
        this.gameRegistry = new LanternGameRegistry(this);
        this.gameRegistry.preRegisterGameObjects();

        // Create the global config
        this.globalConfig = new GlobalConfig(this.configFolder.resolve(GLOBAL_CONFIG));
        this.globalConfig.load();

        // Create the ops config
        this.opsConfig = new UserConfig<>(this.configFolder.resolve(OPS_CONFIG));
        this.opsConfig.load();

        // Create the whitelist config
        this.whitelistConfig = new WhitelistConfig(this.configFolder.resolve(WHITELIST_CONFIG));
        this.whitelistConfig.load();

        // Create the whitelist config
        this.banConfig = new BanConfig(this.configFolder.resolve(BANS_CONFIG));
        this.banConfig.load();
    }

    public void initialize(LanternServer server, Path rootWorldFolder) {
        this.rootWorldFolder = rootWorldFolder;
        this.server = server;

        // Create the channel registrar
        this.channelRegistrar =  new LanternChannelRegistrar(server);

        // Register the game objects
        this.gameDictionary = new LanternGameDictionary();
        this.gameRegistry.registerGameObjects();

        // Create the plugin manager instance
        this.pluginManager = new LanternPluginManager(this, this.pluginsFolder, this.minecraft,
                this.apiContainer, this.implContainer);

        // Create the service manager instance
        this.serviceManager = new SimpleServiceManager(this.pluginManager);

        // Register the config service
        this.configManager = new LanternConfigManager(this.configFolder);

        // Create the scheduler
        this.scheduler = new LanternScheduler();

        // Create the chunk load service
        this.chunkTicketManager = new LanternChunkTicketManager(this.globalConfig);

        // Create the data manager
        this.dataManager = new LanternDataManager();

        // Register the game profile resolver
        this.gameProfileManager = new LanternGameProfileManager();

        this.registerService(WhitelistService.class, this.whitelistConfig);
        this.registerService(BanService.class, this.banConfig);

        this.registerService(UserStorageService.class, new LanternUserStorageService());
        // Register the pagination service
        this.registerService(PaginationService.class, new LanternPaginationService(this));

        // Register the command service
        SimpleCommandManager commandService = new SimpleCommandManager(log(), new LanternCommandDisambiguator(this));
        if (this.registerService(CommandManager.class, commandService)) {
            commandService.register(this.minecraft, new CommandStop(this).build(), "stop", "shutdown");
            commandService.register(this.minecraft, new CommandHelp(this).build(), "help", "?");
            // TODO: Use a different plugin for this command?
            commandService.register(this.minecraft, new CommandVersion(this).build(), "version");
        }
        commandService.register(this.implContainer, LanternCallbackHolder.getInstance().createCommand(),
                LanternCallbackHolder.CALLBACK_COMMAND);

        // Create the teleport helper
        this.teleportHelper = new LanternTeleportHelper();

        // Create the event manager instance
        this.eventManager = new LanternEventManager();

        // Call the construction events
        this.eventManager.post(SpongeEventFactory.createGameConstructionEvent(Cause.of(this), 
                GameState.CONSTRUCTION));

        // Load the plugin instances
        try {
            this.pluginManager.loadPlugins();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while loading the plugins.", e);
        }

        // Load-complete phase
        this.setGameState(GameState.LOAD_COMPLETE);
        this.eventManager.post(SpongeEventFactory.createGameLoadCompleteEvent(Cause.of(this), 
                GameState.LOAD_COMPLETE));

        // Pre-init phase
        this.setGameState(GameState.PRE_INITIALIZATION);
        this.eventManager.post(SpongeEventFactory.createGamePreInitializationEvent(Cause.of(this), 
                GameState.PRE_INITIALIZATION));
        this.serviceManager.potentiallyProvide(PermissionService.class).executeWhenPresent(
                input -> this.server.getConsole().getContainingCollection());

        // Create the default sql service
        this.registerService(SqlService.class, new LanternSqlService());

        // Init phase
        this.setGameState(GameState.INITIALIZATION);
        this.eventManager.post(SpongeEventFactory.createGameInitializationEvent(Cause.of(this), 
                GameState.INITIALIZATION));

        // Provide the default permission service if no custom one is found
        if (!this.serviceManager.provide(PermissionService.class).isPresent()) {
            try {
                final LanternPermissionService service = new LanternPermissionService(this);
                service.getGroupForOpLevel(1).getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT,
                        "minecraft.selector", Tristate.TRUE);
                service.getGroupForOpLevel(2).getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT,
                        "minecraft.commandblock", Tristate.TRUE);
                
                this.serviceManager.setProvider(this.minecraft, PermissionService.class, service);
            } catch (ProviderExistsException e) {
            }
        }

        // Post-init phase
        this.setGameState(GameState.POST_INITIALIZATION);
        this.eventManager.post(SpongeEventFactory.createGamePostInitializationEvent(Cause.of(this), 
                GameState.POST_INITIALIZATION));
    }

    private <T> boolean registerService(Class<T> serviceClass, T serviceImpl) {
        try {
            this.serviceManager.setProvider(this.minecraft, serviceClass, serviceImpl);
            return true;
        } catch (ProviderExistsException e) {
            log().warn("Non-Lantern {} already registered: {}", serviceClass.getSimpleName(), e.getLocalizedMessage());
            return false;
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Gets the plugin container that represents the minecraft (lantern) server.
     * 
     * @return the plugin container
     */
    public PluginContainer getPlugin() {
        return this.minecraft;
    }

    /**
     * Gets the global configuration.
     * 
     * @return the global configuration
     */
    public GlobalConfig getGlobalConfig() {
        return this.globalConfig;
    }

    /**
     * Gets the ops configuration.
     * 
     * @return the ops configuration
     */
    public UserConfig<OpsEntry> getOpsConfig() {
        return this.opsConfig;
    }

    /**
     * Gets the whitelist configuration.
     * 
     * @return the whitelist configuration
     */
    public WhitelistConfig getWhitelistConfig() {
        return this.whitelistConfig;
    }

    /**
     * Gets the ban configuration.
     * 
     * @return the ban configuration
     */
    public BanConfig getBanConfig() {
        return this.banConfig;
    }

    @Override
    public GameState getState() {
        return this.gameState;
    }

    /**
     * Gets the logger of the game.
     * 
     * @return the logger
     */
    public Logger getLogger() {
        return log();
    }

    @Override
    public Server getServer() {
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
    public LanternGameRegistry getRegistry() {
        return this.gameRegistry;
    }

    @Override
    public GameDictionary getGameDictionary() {
        return this.gameDictionary;
    }

    @Override
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    @Override
    public CommandManager getCommandManager() {
        return this.serviceManager.provideUnchecked(CommandManager.class);
    }

    @Override
    public TeleportHelper getTeleportHelper() {
        return this.teleportHelper;
    }

    @Override
    public Platform getPlatform() {
        return this.platform;
    }

    @Override
    public LanternScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public Path getSavesDirectory() {
        return this.rootWorldFolder;
    }

    /**
     * Gets the {@link LanternChunkTicketManager}.
     * 
     * @return the chunk ticket manager
     */
    public LanternChunkTicketManager getChunkTicketManager() {
        return this.chunkTicketManager;
    }

    @Override
    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    /**
     * Gets the {@link LanternChannelRegistrar}.
     * 
     * @return the channel registrar
     */
    public LanternChannelRegistrar getChannelRegistrar() {
        return this.channelRegistrar;
    }

    /**
     * Gets the {@link LanternGameProfileManager}.
     * 
     * @return the game profile manager
     */
    public LanternGameProfileManager getGameProfileManager() {
        return this.gameProfileManager;
    }

    @Override
    public PropertyRegistry getPropertyRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LanternDataManager getDataManager() {
        return this.dataManager;
    }
}
