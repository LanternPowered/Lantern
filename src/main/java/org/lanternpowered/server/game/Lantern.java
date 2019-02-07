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

import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.asset.AssetRepository;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.world.LanternWorldManager;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

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

    public static LanternWorldManager getWorldManager() {
        return LanternGame.game.getServer().getWorldManager();
    }

    private Lantern() {
    }

}
