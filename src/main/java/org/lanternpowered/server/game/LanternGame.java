/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.asset.LanternAssetManager;
import org.lanternpowered.server.command.CommandBan;
import org.lanternpowered.server.command.CommandBanIp;
import org.lanternpowered.server.command.CommandBorder;
import org.lanternpowered.server.command.CommandDeop;
import org.lanternpowered.server.command.CommandDifficulty;
import org.lanternpowered.server.command.CommandGameRule;
import org.lanternpowered.server.command.CommandHelp;
import org.lanternpowered.server.command.CommandListBans;
import org.lanternpowered.server.command.CommandListPlayers;
import org.lanternpowered.server.command.CommandMe;
import org.lanternpowered.server.command.CommandOp;
import org.lanternpowered.server.command.CommandPardon;
import org.lanternpowered.server.command.CommandPardonIp;
import org.lanternpowered.server.command.CommandParticle;
import org.lanternpowered.server.command.CommandParticleEffect;
import org.lanternpowered.server.command.CommandPlaySound;
import org.lanternpowered.server.command.CommandProvider;
import org.lanternpowered.server.command.CommandSay;
import org.lanternpowered.server.command.CommandScoreboard;
import org.lanternpowered.server.command.CommandSetSpawn;
import org.lanternpowered.server.command.CommandStop;
import org.lanternpowered.server.command.CommandStopSound;
import org.lanternpowered.server.command.CommandTell;
import org.lanternpowered.server.command.CommandTime;
import org.lanternpowered.server.command.CommandTitle;
import org.lanternpowered.server.command.CommandVersion;
import org.lanternpowered.server.command.CommandWeather;
import org.lanternpowered.server.command.CommandWhitelist;
import org.lanternpowered.server.command.LanternCommandDisambiguator;
import org.lanternpowered.server.command.LanternCommandManager;
import org.lanternpowered.server.command.test.CommandOpenTestContainer;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.config.LanternConfigManager;
import org.lanternpowered.server.config.user.OpsConfig;
import org.lanternpowered.server.config.user.OpsEntry;
import org.lanternpowered.server.config.user.UserConfig;
import org.lanternpowered.server.config.user.WhitelistConfig;
import org.lanternpowered.server.config.user.ban.BanConfig;
import org.lanternpowered.server.data.LanternDataManager;
import org.lanternpowered.server.data.property.LanternPropertyRegistry;
import org.lanternpowered.server.event.LanternEventManager;
import org.lanternpowered.server.network.channel.LanternChannelRegistrar;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.lanternpowered.server.plugin.LanternServerContainer;
import org.lanternpowered.server.plugin.MinecraftPluginContainer;
import org.lanternpowered.server.plugin.SpongeApiContainer;
import org.lanternpowered.server.profile.LanternGameProfileManager;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.service.LanternServiceListeners;
import org.lanternpowered.server.service.pagination.LanternPaginationService;
import org.lanternpowered.server.service.permission.LanternContextCalculator;
import org.lanternpowered.server.service.permission.LanternPermissionService;
import org.lanternpowered.server.service.sql.LanternSqlService;
import org.lanternpowered.server.service.user.LanternUserStorageService;
import org.lanternpowered.server.util.ReflectionHelper;
import org.lanternpowered.server.world.LanternTeleportHelper;
import org.lanternpowered.server.world.chunk.LanternChunkTicketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.data.property.PropertyRegistry;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.SpongeEventFactoryUtils;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStateEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.SimpleServiceManager;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.rcon.RconService;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.TeleportHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@NonnullByDefault
public class LanternGame implements Game {

    private final static boolean SCAN_CLASSPATH = PropertiesUtil.getProperties().getBooleanProperty("scanClasspath", false);

    public static final String API_NAME = "SpongeAPI";
    public static final String API_ID = "spongeapi";

    public static final String IMPL_NAME = "Lantern";
    public static final String IMPL_ID = "lantern";

    public static final String MINECRAFT_ID = "minecraft";
    public static final String MINECRAFT_NAME = "Minecraft";
    public static final String MINECRAFT_VERSION = "1.10";

    // The name of the config folder
    public static final String CONFIG_FOLDER = "config";

    // The name of the global config file
    public static final String GLOBAL_CONFIG = "global.conf";

    // The name of the ops config file
    public static final String OPS_CONFIG = "ops.json";

    // The name of the whitelist config file
    public static final String WHITELIST_CONFIG = "whitelist.json";

    // The name of the ban config file
    public static final String BANS_CONFIG = "bans.json";

    // The name of the config folder
    public static final String PLUGINS_FOLDER = "plugins";

    // The name of the profile cache file
    public static final String PROFILE_CACHE_FILE = "profile-cache.json";

    // The singleton instance of the game
    static LanternGame game;

    // The server will aim for 20 ticks per second
    public static final int TICKS_PER_SECOND = 20;

    // The amount of milli seconds in one tick
    public static final int TICK_DURATION = 1000 / TICKS_PER_SECOND;

    // The amount of nano seconds in one tick
    public static final long TICK_DURATION_NS = TimeUnit.NANOSECONDS.convert(TICK_DURATION, TimeUnit.MILLISECONDS);

    // The logger of the game
    private static Logger logger = LoggerFactory.getLogger(IMPL_NAME);

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

    // The sync scheduler service
    private SpongeExecutorService syncExecutorService;

    // The chunk load service
    private LanternChunkTicketManager chunkTicketManager;

    // The command manager
    private LanternCommandManager commandManager;

    // The serialization service 
    private LanternDataManager dataManager;

    // The asset manager
    private LanternAssetManager assetManager;

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
    // The game folder
    private final Path gameFolder;

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
        this.gameFolder = new File("").toPath();
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
        this.assetManager = new LanternAssetManager();

        // Pre register some game objects
        this.gameRegistry = new LanternGameRegistry(this);
        this.gameRegistry.registerDefaults();
        this.gameRegistry.earlyRegistry();

        // Create the global config
        this.globalConfig = new GlobalConfig(this.configFolder.resolve(GLOBAL_CONFIG));
        this.globalConfig.load();
        // Save to update possible missing properties
        this.globalConfig.save();

        // Create the ops config
        this.opsConfig = new OpsConfig(this.configFolder.resolve(OPS_CONFIG));
        this.opsConfig.load();

        // Create the whitelist config
        this.whitelistConfig = new WhitelistConfig(this.configFolder.resolve(WHITELIST_CONFIG));
        this.whitelistConfig.load();

        // Create the ban config
        this.banConfig = new BanConfig(this.configFolder.resolve(BANS_CONFIG));
        this.banConfig.load();
    }

    public void initialize(LanternServer server, RconService rconService, Path rootWorldFolder) {
        this.rootWorldFolder = rootWorldFolder;
        this.server = server;

        // Call pre registry phase.
        this.gameRegistry.preRegistry();

        // Create the channel registrar
        this.channelRegistrar =  new LanternChannelRegistrar(server);

        // Register the game objects
        this.gameDictionary = new LanternGameDictionary();

        // Create the plugin manager instance
        this.pluginManager = new LanternPluginManager(this, this.pluginsFolder, this.minecraft,
                this.apiContainer, this.implContainer);

        // Create the event manager instance
        this.eventManager = new LanternEventManager();
        this.eventManager.registerListeners(this.implContainer, LanternServiceListeners.getInstance());

        // Create the service manager instance
        this.serviceManager = new SimpleServiceManager(this.pluginManager);

        // Register the config service
        this.configManager = new LanternConfigManager(this.configFolder);

        // Create the scheduler
        this.scheduler = new LanternScheduler();
        this.syncExecutorService = this.scheduler.createSyncExecutor(this.minecraft);

        // Create the chunk load service
        this.chunkTicketManager = new LanternChunkTicketManager(this.globalConfig);

        // Create the data manager
        this.dataManager = new LanternDataManager();

        // Register the game profile resolver
        this.gameProfileManager = new LanternGameProfileManager(this.configFolder.resolve(PROFILE_CACHE_FILE));

        this.registerService(WhitelistService.class, this.whitelistConfig);
        this.registerService(BanService.class, this.banConfig);
        this.registerService(RconService.class, rconService);

        this.registerService(UserStorageService.class, new LanternUserStorageService());
        // Register the pagination service
        this.registerService(PaginationService.class, new LanternPaginationService());

        // Register the command service
        this.commandManager = new LanternCommandManager(this.getLogger(), new LanternCommandDisambiguator(this));

        final Multimap<PluginContainer, CommandProvider> commandProviders = HashMultimap.create();
        commandProviders.put(this.minecraft, new CommandBan());
        commandProviders.put(this.minecraft, new CommandBanIp());
        commandProviders.put(this.minecraft, new CommandBorder());
        commandProviders.put(this.minecraft, new CommandDeop());
        commandProviders.put(this.minecraft, new CommandDifficulty());
        commandProviders.put(this.minecraft, new CommandGameRule());
        commandProviders.put(this.minecraft, new CommandHelp());
        commandProviders.put(this.minecraft, new CommandListBans());
        commandProviders.put(this.minecraft, new CommandListPlayers());
        commandProviders.put(this.minecraft, new CommandMe());
        commandProviders.put(this.minecraft, new CommandOp());
        commandProviders.put(this.minecraft, new CommandPardon());
        commandProviders.put(this.minecraft, new CommandPardonIp());
        commandProviders.put(this.minecraft, new CommandParticle());
        commandProviders.put(this.implContainer, new CommandParticleEffect());
        commandProviders.put(this.minecraft, new CommandPlaySound());
        commandProviders.put(this.minecraft, new CommandSay());
        commandProviders.put(this.minecraft, new CommandScoreboard());
        commandProviders.put(this.minecraft, new CommandSetSpawn());
        commandProviders.put(this.minecraft, new CommandStop());
        commandProviders.put(this.minecraft, new CommandStopSound());
        commandProviders.put(this.minecraft, new CommandTell());
        commandProviders.put(this.minecraft, new CommandTime());
        commandProviders.put(this.minecraft, new CommandTitle());
        commandProviders.put(this.implContainer, new CommandVersion());
        commandProviders.put(this.minecraft, new CommandWeather());
        commandProviders.put(this.minecraft, new CommandWhitelist());

        // Testing
        commandProviders.put(this.implContainer, new CommandOpenTestContainer());

        for (Map.Entry<PluginContainer, CommandProvider> entry : commandProviders.entries()) {
            final PluginContainer plugin = entry.getKey();
            this.commandManager.register(plugin, entry.getValue().buildSpecFor(plugin), entry.getValue().getAliases());
        }

        // Create the teleport helper
        this.teleportHelper = new LanternTeleportHelper();

        // Call the construction events
        this.postGameStateChange(GameState.CONSTRUCTION, GameConstructionEvent.class);

        // Load the plugin instances
        try {
            this.pluginManager.loadPlugins(SCAN_CLASSPATH);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while loading the plugins.", e);
        }

        // Call pre init phase for registry
        this.gameRegistry.preInit();

        LanternServiceListeners.getInstance().registerServiceCallback(PermissionService.class,
                input -> {
                    this.server.getConsole().getContainingCollection();
                    input.registerContextCalculator(new LanternContextCalculator());
                });

        // Pre-init phase
        this.postGameStateChange(GameState.PRE_INITIALIZATION, GamePreInitializationEvent.class);

        // Create the default sql service
        this.registerService(SqlService.class, new LanternSqlService());

        // Call init phase for registry
        this.gameRegistry.init();

        // Provide the default permission service if no custom one is found
        if (!this.serviceManager.provide(PermissionService.class).isPresent()) {
            final LanternPermissionService service = new LanternPermissionService(this);

            for (Map.Entry<PluginContainer, CommandProvider> entry : commandProviders.entries()) {
                entry.getValue().getOpPermissionLevel().ifPresent(level -> service.getGroupForOpLevel(level).getSubjectData()
                        .setPermission(SubjectData.GLOBAL_CONTEXT, entry.getValue().getPermissionFor(entry.getKey()), Tristate.TRUE));
            }
            // Group level 1 permissions
            SubjectData subjectData = service.getGroupForOpLevel(1).getSubjectData();
            subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, "minecraft.selector", Tristate.TRUE);
            // Group level 2 permissions
            subjectData = service.getGroupForOpLevel(2).getSubjectData();
            subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, "minecraft.commandblock", Tristate.TRUE);

            this.serviceManager.setProvider(this.minecraft, PermissionService.class, service);
        }

        // Init phase
        this.postGameStateChange(GameState.INITIALIZATION, GameInitializationEvent.class);

        // Call post init phase for registry
        this.gameRegistry.postInit();

        // Post-init phase
        this.postGameStateChange(GameState.POST_INITIALIZATION, GamePostInitializationEvent.class);

        // Load-complete phase
        this.postGameStateChange(GameState.LOAD_COMPLETE, GameLoadCompleteEvent.class);
    }

    public <T extends GameStateEvent> void postGameStateChange(GameState gameState, Class<T> eventClass) {
        this.gameState = checkNotNull(gameState, "gameState");
        this.eventManager.post(SpongeEventFactoryUtils.createState(eventClass, this));
    }

    private <T> void registerService(Class<T> serviceClass, T serviceImpl) {
        this.serviceManager.setProvider(this.minecraft, serviceClass, serviceImpl);
    }

    /**
     * Gets the plugin container that represents the minecraft server.
     * 
     * @return the plugin container
     */
    public PluginContainer getMinecraftPlugin() {
        return this.minecraft;
    }

    /**
     * Gets the plugin container that represents the implementation.
     *
     * @return the plugin container
     */
    public PluginContainer getImplementationPlugin() {
        return this.implContainer;
    }

    /**
     * Gets the plugin container that represents the api.
     *
     * @return the plugin container
     */
    public PluginContainer getApiPlugin() {
        return this.apiContainer;
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
        return logger;
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
    public AssetManager getAssetManager() {
        return this.assetManager;
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
        return this.commandManager;
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
        return LanternPropertyRegistry.getInstance();
    }

    @Override
    public LanternDataManager getDataManager() {
        return this.dataManager;
    }

    public Path getConfigDir() {
        return this.configFolder;
    }

    public SpongeExecutorService getSyncExecutorService() {
        return this.syncExecutorService;
    }
}
