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
package org.lanternpowered.server.config

import ninja.leaping.configurate.objectmapping.Setting
import org.lanternpowered.api.text.textOf
import org.lanternpowered.api.util.Tristate
import org.lanternpowered.server.network.ProxyType
import org.lanternpowered.server.util.IpSet

class GlobalConfigObject : ConfigObject() {

    val server by ServerConfigObject.with(name = "server",
            description = "Configuration for the server.")

    val rcon by RconConfigObject.with(name = "rcon",
            description = "Configuration for the rcon server.")

    val query by QueryConfigObject.with(name = "query",
            description = "Configuration for the query server.")

    val metrics by MetricsConfigObject.with(name = "metrics",
            description = "Configuration for the metrics.")

    val chat by ChatConfigObject.with(name = "chat",
            description = "Configuration for the chat.")

    companion object : Factory<GlobalConfigObject> by { GlobalConfigObject() }
}

class RconConfigObject : ConfigObject() {

    var enabled by setting(default = true, name = "enabled",
            description = "Whether the rcon server should be enabled.")

    var ip by setting(default = "", name = "ip",
            description = "The ip address that should be bound, leave it empty to bind to the \"localhost\".")

    var port by setting(default = 25575, name = "port",
            description = "The port that should be bound.")

    var password by setting(default = "", name = "password",
            description = "The password that is required to login.")

    companion object : Factory<RconConfigObject> by { RconConfigObject() }
}

class QueryConfigObject : ConfigObject() {

    var enabled by setting(default = true, name = "enabled",
            description = "Whether the query server should be enabled.")

    var ip by setting(default = "", name = "ip",
            description = "The ip address that should be bound, leave it empty to bind to the \"localhost\".")

    var port by setting(default = 25563, name = "port",
            description = "The port that should be bound.")

    var showPlugins by setting(default = true, name = "show-plugins",
            description = "Whether all the plugins should be added to the query.")

    companion object : Factory<QueryConfigObject> by { QueryConfigObject() }
}

class ServerConfigObject : ConfigObject() {

    var ip by setting(default = "", name = "ip",
            description = "The ip address that should be bound, leave it empty to bind to the \"localhost\".")

    var port by setting(default = 25565, name = "port",
            description = "The port that should be bound.")

    var name by setting(default = "Lantern", name = "name",
            description = "The name of the server.")

    var favicon by setting(default = "favicon.png", name = "favicon",
            description = "The path of the favicon file. The format must be in png and\n" +
                          "the dimension must be 64x64, otherwise it will not work.")

    var onlineMode by setting(default = true, name = "online-mode",
            description = "Whether you want to enable the online mode, it is recommend\n" +
                          "to run the server in online mode.")

    var maxPlayers by setting(default = 20, name = "max-players",
            description = "The maximum amount of players that may join the server.")

    var messageOfTheDay by setting(default = textOf("A lantern minecraft server!"), name = "message-of-the-day",
            description = "This is the message that will be displayed in the server list.")

    var shutdownMessage by setting(default = textOf("Server shutting down."), name = "shutdown-message",
            description = "This is the default message that will be displayed when the server is shut down.")

    var whitelist by setting(default = false, name = "whitelist",
            description = "Whether the white-list is enabled.")

    var playerIdleTimeout by setting(default = 0, name = "player-idle-timeout",
            description = "The player idle timeout in minutes, a value smaller or equal to 0 disables the check.")

    var defaultResourcePack by setting(default = "", name = "default-resource-pack",
            description = "The default resource pack. Leave this empty to disable the default resource pack.")

    var ipBasedContexts by setting(default = mapOf<String, List<IpSet>>(), name = "ip-based-contexts",
            description = "Configuration for ip based permission contexts.")

    var viewDistance by setting(default = ViewDistance.DEFAULT, name = "view-distance",
            description = "The view distance. The value must be between ${ViewDistance.MINIMUM} and ${ViewDistance.MAXIMUM} (inclusive).")

    var preventProxyConnections by setting(default = false, name = "prevent-proxy-connections",
            description = "Whether proxy connections should be prevented. This is only supported in the online mode.")

    var networkCompressionThreshold by setting(default = 256, name = "network-compression-threshold",
            description = "The network compression threshold, set to -1 to disable packet compression.")

    // TODO: Expand explanation of ip based contexts

    val proxy by ProxyConfigObject.with(name = "proxy")

    companion object : Factory<ServerConfigObject> by { ServerConfigObject() }
}

class ProxyConfigObject : ConfigObject() {

    var type by setting(default = ProxyType.NONE, name = "type",
            description = "The type of the proxy, or none if disabled.")

    var securityKey by setting(default = "", name = "security-key",
            description = """
                A security key shared between a proxy and it's server to make sure 
                that they are connecting from your network.
                Is currently only applicable for the LilyPad proxy.
                If you want to disable the security you may leave this field empty.
                """.trimIndent())

    companion object : Factory<ProxyConfigObject> by { ProxyConfigObject() }
}

class MetricsConfigObject : ConfigObject() {

    var globalState by setting(default = Tristate.UNDEFINED, name = "global-state",
            description = "The global collection state that should be respected by all plugins that have " +
                          "no specified collection state. If undefined then it is treated as disabled.")

    var pluginStates by setting(default = mapOf<String, Tristate>(), name = "plugin-states",
            description = "The plugin specific collection states that override the global collection state.")

    companion object : Factory<MetricsConfigObject> by { MetricsConfigObject() }
}

class ChatConfigObject : ConfigObject() {

    val clickableUrls by setting(default = true, name = "clickable-urls",
            description = "Whether URL's send by the client should be automatically made clickable.")

    val maxMessagesPerSecond by setting(default = 10.0, name = "max-messages-per-second",
            description = "The maximum amount of messages you can send per second before it's " +
                          "considered spamming and you'll be kicked.")

    companion object : Factory<ChatConfigObject> by { ChatConfigObject() }
}
