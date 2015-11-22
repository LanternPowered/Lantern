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
package org.lanternpowered.server.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

public final class LanternConfig<T extends LanternConfig.Base> {

    private static final String MAXIMUM_CHUNKS_PER_TICKET = "maximum-chunks-per-ticket";
    private static final String MAXIMUM_TICKET_COUNT = "maximum-ticket-count";
    private static final String PLAYER_TICKET_COUNT = "player-ticket-count";
    private static final String CHUNK_LOADING = "chunk-loading";
    private static final String DEFAULTS = "defaults";
    private static final String OVERRIDES = "overrides";
    private static final String ENABLED = "enabled";

    private ConfigurationLoader<CommentedConfigurationNode> loader; 
    private CommentedConfigurationNode root = SimpleCommentedConfigurationNode.root(
            ConfigurationOptions.defaults());
    private ObjectMapper<T>.BoundInstance configMapper;
    private final T configBase;
    private final Path path;

    public LanternConfig(T configBase, Path path) {
        this.configBase = configBase;
        this.path = path;
        try {
            Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                Files.createFile(path);
            }

            this.loader = HoconConfigurationLoader.builder().setPath(path).build();
            this.configMapper = ObjectMapper.forObject(configBase);

            this.reload();
            this.save();
        } catch (Exception e) {
            LanternGame.log().error("Failed to initialize configuration", e);
        }
    }

    /**
     * Gets the path of the file.
     * 
     * @return the path
     */
    public Path getPath() {
        return this.path;
    }

    /**
     * Gets the plugin base instance.
     * 
     * @return the plugin base
     */
    public T getBase() {
        return this.configBase;
    }

    /**
     * Reloads the configuration file.
     */
    public void reload() {
        try {
            this.root = this.loader.load(ConfigurationOptions.defaults()
                    .setSerializers(TypeSerializers.getDefaultSerializers().newChild().registerType(
                            TypeToken.of(Text.class), new TextTypeSerializer())));
            this.configMapper.populate(this.root);
        } catch (Exception e) {
            LanternGame.log().error("Failed to load configuration", e);
        }
    }

    /**
     * Saves the configuration file.
     */
    public void save() {
        try {
            this.configMapper.serialize(this.root);
            this.loader.save(this.root);
        } catch (IOException | ObjectMappingException e) {
            LanternGame.log().error("Failed to save configuration", e);
        }
    }

    public abstract static class Base {
    }

    public interface ChunkLoadingTickets {

        /**
         * Gets the maximum amount of chunks that can
         * be forced to load per ticket.
         * 
         * @return the maximum amount of chunks per ticket
         */
        int getMaximumChunksPerTicket();

        /**
         * Gets the maximum amount of tickets that a
         * plugin can request.
         * 
         * @return the maximum count of tickets
         */
        int getMaximumTicketCount();
    }

    public interface ChunkLoadingConfig {

       /**
        * Gets the chunk loading tickets configuration for the specified plugin.
        * 
        * @return the chunk loading tickets configuration
        */
       ChunkLoadingTickets getChunkLoadingTickets(String plugin);
    }

    public static final class GlobalConfig extends Base implements ChunkLoadingConfig {

        @Setting(value = "server", comment = "Configuration for the server.")
        private Server server = new Server();

        @Setting(value = "worlds", comment = "Configuration for the worlds.")
        private World worlds = new World();

        @Override
        public ChunkLoadingTickets getChunkLoadingTickets(String plugin) {
            return this.worlds.chunkLoading.getChunkLoadingTickets(plugin);
        }

        /**
         * Gets the server configuration section.
         * 
         * @return the server configuration
         */
        public Server getServer() {
            return this.server;
        }

        /**
         * Gets the world configuration section.
         * 
         * @return the world configuration
         */
        public World getWorld() {
            return this.worlds;
        }
    }

    @ConfigSerializable
    public static final class World {

        @Setting(value = CHUNK_LOADING, comment = "Configuration for the chunk loading control.")
        private ChunkLoading chunkLoading = new ChunkLoading();

        @Setting(value = "root-folder", comment = "The name of the root world folder.")
        private String worldFolder = "world";

        public String getWorldFolder() {
            return this.worldFolder;
        }
    }

    @ConfigSerializable
    public static final class Server {

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

        public String getIp() {
            return this.ip;
        }

        public int getPort() {
            return this.port;
        }

        public int getMaxPlayers() {
            return this.maxPlayers;
        }

        public String getName() {
            return this.name;
        }

        public String getFavicon() {
            return this.favicon;
        }

        public Text getMotd() {
            return this.motd;
        }

        public boolean isOnlineMode() {
            return this.onlineMode;
        }
    }

    public static final class WorldConfig extends Base implements ChunkLoadingConfig {

        @Setting(value = CHUNK_LOADING, comment = "Configuration for the chunk loading control.")
        private WorldChunkLoading chunkLoading = new WorldChunkLoading();

        // The global config for fallback
        private final GlobalConfig globalConfig;

        public WorldConfig(GlobalConfig globalConfig) {
            this.globalConfig = globalConfig;
        }

        @Override
        public ChunkLoadingTickets getChunkLoadingTickets(String plugin) {
            if (this.chunkLoading.enabled) {
                return this.chunkLoading.getChunkLoadingTickets(plugin);
            }
            return this.globalConfig.getChunkLoadingTickets(plugin);
        }
    }

    @ConfigSerializable
    public static class ChunkLoading {

        private static final MinecraftChunkLoadingTickets MINECRAFT = new MinecraftChunkLoadingTickets();

        @Setting(value = DEFAULTS, comment = "Default configuration for chunk loading control.")
        private GlobalChunkLoadingTickets defaults = new GlobalChunkLoadingTickets();

        @Setting(value = OVERRIDES, comment = "Plugin specific configuration for chunk loading control.")
        private Map<String, PluginChunkLoadingTickets> pluginOverrides = Maps.newHashMap();

        /**
         * Gets the chunk loading tickets configuration for the specified plugin.
         * 
         * @return the chunk loading tickets configuration
         */
        public ChunkLoadingTickets getChunkLoadingTickets(String plugin) {
            // Minecraft has no limits
            if (plugin.equalsIgnoreCase("Minecraft")) {
                return MINECRAFT;
            }
            // Check for overridden configuration
            if (this.pluginOverrides.containsKey(plugin)) {
                return this.pluginOverrides.get(plugin);
            }
            // Fall back to default if not found
            return this.defaults;
        }
    }

    @ConfigSerializable
    public static class WorldChunkLoading extends ChunkLoading {

        @Setting(value = ENABLED, comment =
                "Whether this configuration file should override the globally specified chunk\n " +
                "loading settings, if set false, this sections won't affect anything.")
        private boolean enabled = false;

        /**
         * Gets whether this section is enabled.
         * 
         * @return is enabled
         */
        public boolean isEnabled() {
            return this.enabled;
        }
    }

    @ConfigSerializable
    public static class PluginChunkLoadingTickets implements ChunkLoadingTickets {

        @Setting(value = MAXIMUM_CHUNKS_PER_TICKET, comment =
                "Maximum chunks per ticket for the plugin.")
        private int maximumChunksPerTicket = 25;

        @Setting(value = MAXIMUM_TICKET_COUNT, comment =
                "Maximum ticket count for the mod. Zero disables chunkloading capabilities.")
        private int maximumTicketCount = 200;

        @Override
        public int getMaximumChunksPerTicket() {
            return this.maximumChunksPerTicket;
        }

        @Override
        public int getMaximumTicketCount() {
            return this.maximumTicketCount;
        }
    }

    /**
     * Global/default settings of the chunk loading, all these settings are available in
     * the global config and the world specific configs.
     */
    @ConfigSerializable
    public static class GlobalChunkLoadingTickets implements ChunkLoadingTickets {

        @Setting(value = MAXIMUM_CHUNKS_PER_TICKET, comment =
                "The default maximum number of chunks a plugin can force, per ticket, for a plugin\n " +
                "without an override. This is the maximum number of chunks a single ticket can force.")
        private int maximumChunksPerTicket = 25;

        @Setting(value = MAXIMUM_TICKET_COUNT, comment =
                "The default maximum ticket count for a plugin which does not have an override\n " +
                "in this file. This is the number of chunk loading requests a plugin is allowed to make.")
        private int maximumTicketCount = 200;

        @Setting(value = PLAYER_TICKET_COUNT, comment =
                "The number of tickets a player can be assigned instead of a plugin. This is shared\n" +
                "across all plugins.")
        private int playerTicketCount = 500;

        @Override
        public int getMaximumChunksPerTicket() {
            return this.maximumChunksPerTicket;
        }

        @Override
        public int getMaximumTicketCount() {
            return this.playerTicketCount;
        }

        /**
         * Gets the maximum amount of tickets that can be requested
         * per player.
         * 
         * @return the player ticket count
         */
        public int getPlayerTicketCount() {
            return this.playerTicketCount;
        }
    }

    /**
     * The internal Minecraft plugin has no chunk loading limits.
     */
    public static final class MinecraftChunkLoadingTickets implements ChunkLoadingTickets {

        @Override
        public int getMaximumChunksPerTicket() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaximumTicketCount() {
            return Integer.MAX_VALUE;
        }
    }
}
