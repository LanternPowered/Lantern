package org.lanternpowered.server.game;

import java.io.File;
import java.util.Locale;

import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.command.CommandHelp;
import org.lanternpowered.server.command.CommandStop;
import org.lanternpowered.server.event.LanternEventManager;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.lanternpowered.server.plugin.MinecraftPluginContainer;
import org.lanternpowered.server.service.pagination.LanternPaginationService;
import org.lanternpowered.server.service.scheduler.LanternScheduler;
import org.lanternpowered.server.world.LanternTeleportHelper;
import org.lanternpowered.server.world.chunk.LanternChunkLoadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.SimpleServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.command.SimpleCommandService;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.scheduler.SchedulerService;
import org.spongepowered.api.service.world.ChunkLoadService;
import org.spongepowered.api.util.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.world.TeleportHelper;

public class LanternGame implements Game {

    // The singleton instance of the game
    private static LanternGame game;

    // The server will aim for 20 ticks per second
    public static final int TICKS_PER_SECOND = 20;

    // The amount of milli seconds in one tick
    public static final int TICK_DURATION = 1000 / TICKS_PER_SECOND;

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
    private final LanternPlatform platform = new LanternPlatform();

    // The server
    private LanternServer server;

    // The plugin manager
    private LanternPluginManager pluginManager;

    // The event manager
    private LanternEventManager eventManager;

    // The service manager
    private ServiceManager serviceManager;

    // The game registry
    private LanternGameRegistry gameRegistry;

    // The scheduler
    private LanternScheduler scheduler;

    // The chunk load service
    private ChunkLoadService chunkLoadService;

    // The teleport helper
    private TeleportHelper teleportHelper;

    // The minecraft plugin instance
    private PluginContainer minecraft;

    // The folder where the worlds are saved
    private File worldsFolder;

    public LanternGame() {
        if (game != null) {
            throw new IllegalStateException("The game can only be initialized once!");
        }
        game = this;
    }

    public void initialize(LanternServer server, File pluginsFolder, File worldsFolder) {
        this.worldsFolder = worldsFolder;
        this.server = server;

        // Create the plugin that represents minecraft
        this.minecraft = new MinecraftPluginContainer(this);

        // Register the game objects
        this.gameRegistry = new LanternGameRegistry(this);
        this.gameRegistry.registerGameObjects();

        // Create the plugin manager instance
        this.pluginManager = new LanternPluginManager(this, pluginsFolder, this.minecraft);

        // Create the service manager instance
        this.serviceManager = new SimpleServiceManager(this.pluginManager);

        // Create the scheduler
        this.scheduler = new LanternScheduler();
        if (!this.registerService(SchedulerService.class, this.scheduler)) {
            throw new ExceptionInInitializerError("Cannot continue with a Non-Lantern Scheduler!");
        }

        // Create the chunk load service
        this.chunkLoadService = new LanternChunkLoadService();
        if (!this.registerService(ChunkLoadService.class, this.chunkLoadService)) {
            throw new ExceptionInInitializerError("Cannot continue with a Non-Lantern ChunkLoadService!");
        }

        // Register the pagination service
        this.registerService(PaginationService.class, new LanternPaginationService(this));

        // Register the command service
        SimpleCommandService commandService = new SimpleCommandService(this, log(),
                SimpleDispatcher.FIRST_DISAMBIGUATOR); // TODO: Use custom disambiguator like in sponge
        if (this.registerService(CommandService.class, commandService)) {
            commandService.register(this.minecraft, new CommandStop(this).build(), "stop", "shutdown");
            commandService.register(this.minecraft, new CommandHelp(this).build(), "help", "?");
        }

        // Create the teleport helper
        this.teleportHelper = new LanternTeleportHelper();

        // Create the event manager instance
        this.eventManager = new LanternEventManager();

        // Load the default translations
        this.gameRegistry.getTranslationManager().addResourceBundle("translations/en_US", Locale.ENGLISH);

        // Load the plugin instances
        this.pluginManager.loadPlugins();
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
    public ServiceManager getServiceManager() {
        return this.serviceManager;
    }

    @Override
    public CommandService getCommandDispatcher() {
        return this.serviceManager.provideUnchecked(CommandService.class);
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
    public File getSavesDirectory() {
        return this.worldsFolder;
    }

    /**
     * Gets the {@link ChunkLoadService}.
     * 
     * @return the chunk load service
     */
    public ChunkLoadService getChunkLoadService() {
        return this.chunkLoadService;
    }
}
