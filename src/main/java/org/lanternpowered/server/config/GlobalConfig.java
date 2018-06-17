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
package org.lanternpowered.server.config;

import static org.lanternpowered.server.config.ConfigConstants.ENABLED;
import static org.lanternpowered.server.config.world.WorldConfig.MAX_VIEW_DISTANCE;
import static org.lanternpowered.server.config.world.WorldConfig.MIN_VIEW_DISTANCE;
import static org.lanternpowered.server.network.vanilla.message.handler.play.HandlerPlayInChatMessage.URL_ARGUMENT;

import com.flowpowered.math.GenericMath;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.lanternpowered.server.config.world.chunk.ChunkLoading;
import org.lanternpowered.server.config.world.chunk.ChunkLoadingConfig;
import org.lanternpowered.server.config.world.chunk.ChunkLoadingTickets;
import org.lanternpowered.server.config.world.chunk.GlobalChunkLoading;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.network.ProxyType;
import org.lanternpowered.server.util.IpSet;
import org.lanternpowered.server.util.functions.Predicates;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nullable;

@Singleton
public class GlobalConfig extends ConfigBase implements ChunkLoadingConfig {

    private static final String FILE_NAME = "global.conf";

    @Inject
    private GlobalConfig(@Named(DirectoryKeys.CONFIG) Path configFolder) throws IOException {
        super(configFolder.resolve(FILE_NAME), true);
    }

    @Setting(value = "server", comment = "Configuration for the server.")
    private Server server = new Server();

    @Setting(value = "worlds", comment = "Configuration for the worlds.")
    private World worlds = new World();

    @Setting(value = "commands")
    private Commands commands = new Commands();

    @Setting(value = "rcon", comment = "Configuration for the rcon server.")
    private Rcon rcon = new Rcon();

    @Setting(value = "query", comment = "Configuration for the query server.")
    private Query query = new Query();

    @Setting(value = "chat", comment = "Configuration for the chat.")
    private Chat chat = new Chat();

    @ConfigSerializable
    private static class Commands {

        @Setting(value = "aliases", comment = "A mapping from unqualified command alias to plugin id"
                + " of the plugin that should handle a certain command")
        private Map<String, String> aliases = new HashMap<>();
    }

    @ConfigSerializable
    private static final class Query {

        @Setting(value = ENABLED, comment = "Whether the query server should be enabled.")
        private boolean enabled = false;

        @Setting(value = "show-plugins", comment = "Whether all the plugins should be added to the query.")
        private boolean showPlugins = true;

        @Setting(value = "port", comment = "The port that should be bound.")
        private int port = 25563;

        @Setting(value = "network-transport", comment = "Settings related to networking transport.")
        private NetworkTransport networkTransport = new NetworkTransport();
    }

    @ConfigSerializable
    private static final class Rcon {

        @Setting(value = ENABLED, comment = "Whether the rcon server should be enabled.")
        private boolean enabled = false;

        @Setting(value = "password", comment = "The password that is required to login.")
        private String password = "";

        @Setting(value = "port", comment = "The port that should be bound.")
        private int port = 25575;

        @Setting(value = "network-transport", comment = "Settings related to networking transport.")
        private NetworkTransport networkTransport = new NetworkTransport();
    }

    @ConfigSerializable
    private static final class Server {

        @Setting(value = "ip", comment =
                "The ip address that should be bound, leave it empty\n " +
                "to bind to the \"localhost\"")
        private String ip = "";

        @Setting(value = "port", comment = "The port that should be bound.")
        private int port = 25565;

        @Setting(value = "network-transport", comment = "Settings related to networking transport.")
        private NetworkTransport networkTransport = new NetworkTransport();

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
        private Text motd = Text.of("A lantern minecraft server!");

        @Setting(value = "shutdown-message", comment =
                "This is the default message that will be displayed when the server is shut down.")
        private Text shutdownMessage = Text.of("Server shutting down.");

        @Setting(value = "network-compression-threshold")
        private int networkCompressionThreshold = 256;

        @Setting(value = "chat-spam-threshold", comment =
                "The minimum time between messages (in milliseconds) when they will be considered spam.")
        private int chatSpamThreshold = 200;

        @Setting(value = "player-idle-timeout", comment =
                "The player idle timeout in minutes, a value smaller or equal to 0 disables the check.")
        private int playerIdleTimeout = 0;

        // Some context related stuff, check this issue for more information
        // https://github.com/SpongePowered/SpongeCommon/commit/71220742baf4b0317ddefe625b12cc64a7ec9084
        // TODO: Move this?
        @Setting(value = "ip-sets")
        private Map<String, List<IpSet>> ipSets = new HashMap<>();

        // TODO: Move this?
        @Setting(value = "op-permission-level", comment = "The default op level of all the operators.")
        private int opPermissionLevel = 4;

        @Setting(value = "white-list", comment = "Whether the white-list is enabled.")
        private boolean whitelist = false;

        @Setting(value = "proxy")
        private Proxy proxy = new Proxy();

        @Setting(value = "default-resource-pack", comment = "The default resource pack.\nLeave this empty to disable the default resource pack.")
        private String defaultResourcePack = "";

        @Setting(value = "prevent-proxy-connections", comment = "Whether proxy connections should be prevented.\n"
                + "This is only supported in the online mode.")
        private boolean preventProxyConnections = false;
    }

    @ConfigSerializable
    public static final class Chat {

        @Setting(value = "urls", comment = "Configuration for the urls formatting in chat.")
        private Urls urls;

        @ConfigSerializable
        public static final class Urls {

            @Setting(value = "enabled", comment = "Whether the url replacement is enabled.")
            private boolean enabled = true;

            @Setting(value = "template", comment = "The template of the url replacement. The {url} argument will be replaced by the URL.")
            private TextTemplate template = TextTemplate.of(TextColors.BLUE, TextStyles.UNDERLINE, TextTemplate.arg(URL_ARGUMENT));

            /**
             * Gets whether the url formatting is enabled.
             *
             * @return Is enabled
             */
            public boolean isEnabled() {
                return this.enabled;
            }

            /**
             * Gets the {@link TextTemplate}.
             *
             * @return The template
             */
            public TextTemplate getTemplate() {
                return this.template;
            }
        }

        /**
         * Gets the {@link Urls} component.
         *
         * @return The urls
         */
        public Urls getUrls() {
            return this.urls;
        }
    }

    @ConfigSerializable
    private static final class Proxy {

        @Setting(value = "type", comment = "The type of the proxy, or none if disabled.")
        private ProxyType type = ProxyType.NONE;

        @Setting(value = "security-key", comment =
                "A security key shared between a proxy and it's server to make\n" +
                "sure that they are connecting from your network.\n" +
                "Is currently only applicable for the LilyPad proxy.\n" +
                "If you want to disable the security you may leave this field empty.")
        private String securityKey = "";
    }

    @ConfigSerializable
    private static final class World {

        @Setting(value = ChunkLoading.CHUNK_LOADING, comment = "Configuration for the chunk loading control.")
        private GlobalChunkLoading chunkLoading = new GlobalChunkLoading();

        @Setting(value = "root-folder", comment = "The name of the root world folder.")
        private String worldFolder = "world";

        @Setting(
                value = "view-distance",
                comment = "The view distance."
                        + "\nThe value must be greater than or equal to " + MIN_VIEW_DISTANCE + " and less than or equal to " + MAX_VIEW_DISTANCE
        )
        private int viewDistance = 10;
    }

    @ConfigSerializable
    public static final class NetworkTransport {

        @Setting(value = "allow-epoll", comment = "Enables Epoll if it's supported by the os.")
        private boolean allowEpoll = true;

        @Setting(value = "allow-kqueue", comment = "Enables KQueue if it's supported by the os.")
        private boolean allowKQueue = true;

        public boolean allowsEpoll() {
            return this.allowEpoll;
        }

        public boolean allowsKQueue() {
            return this.allowKQueue;
        }
    }

    @Override
    public void load() throws IOException {
        // An ugly fix, but good enough for now,
        // colors are loaded after injection, this avoids
        // the creation of text objects until needed
        if (this.chat.urls == null) {
            this.chat.urls = new Chat.Urls();
        }
        super.load();
        // Clamp the view distance
        setViewDistance(getViewDistance());
    }

    public int getViewDistance() {
        return this.worlds.viewDistance;
    }

    public void setViewDistance(int viewDistance) {
        this.worlds.viewDistance = GenericMath.clamp(viewDistance, MIN_VIEW_DISTANCE, MAX_VIEW_DISTANCE);
    }

    public Chat getChat() {
        return this.chat;
    }

    public String getProxySecurityKey() {
        return this.server.proxy.securityKey;
    }

    public ProxyType getProxyType() {
        return this.server.proxy.type;
    }

    public int getChatSpamThreshold() {
        return this.server.chatSpamThreshold;
    }

    public boolean isWhitelistEnabled() {
        return this.server.whitelist;
    }

    public void setWhitelistEnabled(boolean enabled) {
        this.server.whitelist = enabled;
    }

    public Map<String, String> getCommandAliases() {
        return this.commands.aliases;
    }

    public Map<String, Predicate<InetAddress>> getIpSets() {
        return ImmutableMap.copyOf(Maps.transformValues(this.server.ipSets, Predicates::and));
    }

    @Nullable
    public Predicate<InetAddress> getIpSet(String name) {
        return this.server.ipSets.containsKey(name) ? Predicates.and(this.server.ipSets.get(name)) : null;
    }

    public int getDefaultOpPermissionLevel() {
        return this.server.opPermissionLevel;
    }

    public Text getShutdownMessage() {
        return this.server.shutdownMessage;
    }

    public int getPlayerTicketCount() {
        return this.worlds.chunkLoading.getPlayerTicketCount();
    }

    public String getRootWorldFolder() {
        return this.worlds.worldFolder;
    }

    public String getServerIp() {
        return this.server.ip;
    }

    public int getNetworkCompressionThreshold() {
        return this.server.networkCompressionThreshold;
    }

    public int getRconPort() {
        return this.rcon.port;
    }

    public String getRconPassword() {
        return this.rcon.password;
    }

    public boolean isRconEnabled() {
        return this.rcon.enabled;
    }

    public int getQueryPort() {
        return this.query.port;
    }

    public boolean isQueryEnabled() {
        return this.query.enabled;
    }

    public boolean getShowPluginsToQuery() {
        return this.query.showPlugins;
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

    public boolean shouldPreventProxyConnections() {
        return this.server.preventProxyConnections;
    }

    @Override
    public ChunkLoadingTickets getChunkLoadingTickets(String plugin) {
        return this.worlds.chunkLoading.getChunkLoadingTickets(plugin);
    }

    public int getPlayerIdleTimeout() {
        return this.server.playerIdleTimeout;
    }

    public void setPlayerIdleTimeout(int playerIdleTimeout) {
        this.server.playerIdleTimeout = playerIdleTimeout;
    }

    public NetworkTransport getServerNetworkTransport() {
        return this.server.networkTransport;
    }

    public NetworkTransport getRconNetworkTransport() {
        return this.rcon.networkTransport;
    }

    public NetworkTransport getQueryNetworkTransport() {
        return this.query.networkTransport;
    }

    public String getDefaultResourcePack() {
        return this.server.defaultResourcePack;
    }
}
