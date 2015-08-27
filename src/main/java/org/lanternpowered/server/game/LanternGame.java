package org.lanternpowered.server.game;

import java.io.File;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.service.scheduler.SchedulerService;
import org.spongepowered.api.world.TeleportHelper;

import com.google.inject.Singleton;

@Singleton
public class LanternGame implements Game {

    // The singleton instance of the game
    private static LanternGame game;

    // The server will aim for 20 ticks per second
    public static final int TICKS_PER_SECOND = 20;

    // The amount of milli seconds in one tick
    public static final int TICK_DURATION = 1000 / TICKS_PER_SECOND;

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
        return game.logger;
    }

    /**
     * Gets the internal plugin of the game.
     * 
     * @return the plugin
     */
    public static Object plugin() {
        return game.plugin;
    }

    // The server
    //private final LanternServer server;

    // The platform
    private final LanternPlatform platform = new LanternPlatform();

    // The logger
    private Logger logger;

    // The internal plugin instance
    private Object plugin;

    public LanternGame(/*LanternServer server, */Logger logger, Object internalPlugin) {
        this.plugin = internalPlugin;
        this.logger = logger;
        //this.server = server;

        if (game != null) {
            throw new IllegalStateException("The game can only be initialized once!");
        }
        game = this;
    }

    @Override
    public Server getServer() {
        //return this.server;
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PluginManager getPluginManager() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EventManager getEventManager() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LanternGameRegistry getRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceManager getServiceManager() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CommandService getCommandDispatcher() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TeleportHelper getTeleportHelper() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Platform getPlatform() {
        return this.platform;
    }

    @Override
    public SchedulerService getScheduler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public File getSavesDirectory() {
        // TODO Auto-generated method stub
        return null;
    }

}
