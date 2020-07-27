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
package org.lanternpowered.server.game;

import org.lanternpowered.server.asset.AssetRepository;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.world.LanternWorldManagerOld;
import org.slf4j.Logger;
import org.spongepowered.plugin.PluginContainer;

public class Lantern {

    public static Logger getLogger() {
        return LanternGame.game.getLogger();
    }

    public static org.apache.logging.log4j.Logger getLog4jLogger() {
        return LanternGame.game.getLog4jLogger();
    }

    public static PluginContainer getMinecraftPlugin() {
        return LanternGame.game.getMinecraftPlugin();
    }

    public static PluginContainer getImplementationPlugin() {
        return LanternGame.game.getImplementationPlugin();
    }

    public static PluginContainer getSpongePlugin() {
        return LanternGame.game.getSpongePlugin();
    }

    public static AssetRepository getAssetRepository() {
        return LanternGame.game.getAssetManager().getRepository();
    }

    public static LanternGame getGame() {
        return LanternGame.game;
    }

    public static LanternScheduler getAsyncScheduler() {
        return LanternGame.game.getAsyncScheduler();
    }

    public static LanternScheduler getSyncScheduler() {
        return LanternGame.game.getSyncScheduler();
    }

    public static LanternGameRegistry getRegistry() {
        return LanternGame.game.getRegistry();
    }

    public static LanternServer getServer() {
        return LanternGame.game.getServer();
    }

    public static LanternWorldManagerOld getWorldManager() {
        return LanternGame.game.getServer().getWorldManager();
    }

    private Lantern() {
    }

}
