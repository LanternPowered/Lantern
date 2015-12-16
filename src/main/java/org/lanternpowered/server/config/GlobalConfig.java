/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.config;

import java.io.IOException;
import java.nio.file.Path;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import org.lanternpowered.server.config.world.chunk.ChunkLoading;
import org.lanternpowered.server.config.world.chunk.ChunkLoadingConfig;
import org.lanternpowered.server.config.world.chunk.ChunkLoadingTickets;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

public class GlobalConfig extends ConfigBase implements ChunkLoadingConfig {

    public GlobalConfig(Path path) throws IOException {
        super(path);
    }

    @Setting(value = "server", comment = "Configuration for the server.")
    private Server server = new Server();

    @Setting(value = "worlds", comment = "Configuration for the worlds.")
    private World worlds = new World();

    @ConfigSerializable
    private static final class Server {

        @Setting(value = "ip", comment =
                "The ip address that should be bound, leave it empty\n " +
                "to bind to the \"localhost\"")
        private String ip = "";

        @Setting(value = "port", comment = "The port that should be bound.")
        private int port = 25565;

        @Setting(value = "name", comment = "The name of the server.")
        private String name = "Lantern Server";

        @Setting(value = "favicon", comment =
                "The path of the favicon file. The format must be in png and\n " +
                "the dimension must be 64x64, otherwise will it not work.")
        private String favicon = "favicon.png";

        @Setting(value = "online-mode", comment =
                "Whether you want to enable the online mode, it is recommend\n " +
                "to run the server in online modus.")
        private boolean onlineMode = true;

        @Setting(value = "max-players", comment =
                "The maximum amount of players that may join the server.")
        private int maxPlayers = 20;

        @Setting(value = "message-of-the-day", comment =
                "This is the message that will be displayed in the\n " +
                "server list.")
        private Text motd = Texts.of("A lantern minecraft server!");
    }

    @ConfigSerializable
    private static final class World {

        @Setting(value = ChunkLoading.CHUNK_LOADING, comment = "Configuration for the chunk loading control.")
        private ChunkLoading chunkLoading = new ChunkLoading();

        @Setting(value = "root-folder", comment = "The name of the root world folder.")
        private String worldFolder = "world";
    }

    public String getRootWorldFolder() {
        return this.worlds.worldFolder;
    }

    public String getServerIp() {
        return this.server.ip;
    }

    public int getServerPort() {
        return this.server.port;
    }

    public int getMaxPlayers() {
        return this.server.maxPlayers;
    }

    public String getServerName() {
        return this.server.name;
    }

    public String getFavicon() {
        return this.server.favicon;
    }

    public Text getMotd() {
        return this.server.motd;
    }

    public boolean isOnlineMode() {
        return this.server.onlineMode;
    }

    @Override
    public ChunkLoadingTickets getChunkLoadingTickets(String plugin) {
        return this.worlds.chunkLoading.getChunkLoadingTickets(plugin);
    }
}
