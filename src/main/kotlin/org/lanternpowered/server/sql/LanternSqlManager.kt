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
package org.lanternpowered.server.sql

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.h2.engine.ConnectionInfo
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.util.collections.immutableMapOf
import org.lanternpowered.api.util.optional.emptyOptional
import org.spongepowered.api.config.ConfigManager
import org.spongepowered.api.sql.SqlManager
import java.net.URLDecoder
import java.nio.file.Paths
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Optional
import java.util.Properties
import java.util.regex.Pattern
import javax.sql.DataSource

/**
 * Implementation of a SQL-using service.
 *
 * This implementation does a few interesting things
 *  - It's thread-safe
 *  - It allows applying additional driver-specific connection
 *    properties -- this allows us to do some light performance tuning in
 *    cases where we don't want to be as conservative as the driver developers
 *  - Caches DataSources. This cache is currently never cleared of stale entries
 *    -- if some plugin makes database connections to a ton of different databases
 *    we may want to implement this, but it is kinda unimportant.
 */
class LanternSqlManager(private val configManager: ConfigManager) : SqlManager {

    private val protocolSpecificProperties: Map<String, Properties>
    private val pathCanonicalizers: Map<String, (PluginContainer, String) -> String>

    init {
        val mySqlProps = Properties()
        // Config options based on:
        // http://assets.en.oreilly.com/1/event/21/Connector_J%20Performance%20Gems%20Presentation.pdf
        mySqlProps.setProperty("useConfigs", "maxPerformance")

        this.protocolSpecificProperties = immutableMapOf(
                "com.mysql.jdbc.Driver" to mySqlProps,
                "org.mariadb.jdbc.Driver" to mySqlProps
        )

        this.pathCanonicalizers = immutableMapOf("h2" to { plugin, orig ->
            @Suppress("NAME_SHADOWING")
            var orig = orig
            val h2Info = ConnectionInfo(orig)
            if (!h2Info.isPersistent || h2Info.isRemote)
                return@to orig
            orig = orig.removePrefix("file:")
            val origPath = Paths.get(orig)
            if (origPath.isAbsolute) {
                origPath.toString()
            } else {
                this.configManager.getPluginConfig(plugin).directory.resolve(orig).toAbsolutePath().toString()
            }
        })
    }

    private val connectionCache = Caffeine.newBuilder()
            .removalListener { _: ConnectionInfo?, value: HikariDataSource?, _: RemovalCause? -> value?.close() }
            .build { key: ConnectionInfo ->
                val config = HikariConfig()
                config.username = key.user
                config.password = key.password
                config.driverClassName = key.driverClassName
                // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing for info on pool sizing
                config.maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1
                val driverSpecificProperties = protocolSpecificProperties[key.driverClassName]
                if (driverSpecificProperties != null)
                    config.dataSourceProperties = driverSpecificProperties
                config.jdbcUrl = key.authlessUrl
                HikariDataSource(config)
            }

    private fun getDataSource0(plugin: PluginContainer?, jdbcConnection: String): DataSource {
        val url = getConnectionUrlFromAlias(jdbcConnection).orElse(jdbcConnection)
        val info = connectionInfoFromUrl(plugin, url)
        return this.connectionCache[info]!!
    }

    override fun getDataSource(jdbcConnection: String): DataSource = getDataSource0(null, jdbcConnection)
    override fun getDataSource(plugin: PluginContainer, jdbcConnection: String): DataSource = getDataSource0(plugin, jdbcConnection)

    fun close() {
        this.connectionCache.invalidateAll()
    }

    private val connectionUrlRegex = Pattern.compile("(?:jdbc:)?([^:]+):(//)?(?:([^:]+)(?::([^@]+))?@)?(.*)")

    /**
     * Extracts the connection info from a JDBC url with additional authentication information as specified in [SqlManager].
     *
     * @param container The plugin to put a path relative to
     * @param fullUrl The full JDBC URL as specified in SqlService
     * @return A constructed ConnectionInfo object using the info from the provided URL
     * @throws SQLException If the driver for the given URL is not present
     */
    private fun connectionInfoFromUrl(container: PluginContainer?, fullUrl: String): ConnectionInfo {
        val match = connectionUrlRegex.matcher(fullUrl)
        require(match.matches()) { "URL $fullUrl is not a valid JDBC URL" }
        val protocol = match.group(1)
        val hasSlashes = match.group(2) != null
        val user = urlDecode(match.group(3))
        val pass = urlDecode(match.group(4))
        var serverDatabaseSpecifier = match.group(5)
        val derelativizer = pathCanonicalizers[protocol]
        if (container != null && derelativizer != null)
            serverDatabaseSpecifier = derelativizer(container, serverDatabaseSpecifier)
        val unauthedUrl = "jdbc:" + protocol + (if (hasSlashes) "://" else ":") + serverDatabaseSpecifier
        val driverClass = DriverManager.getDriver(unauthedUrl).javaClass.canonicalName
        return ConnectionInfo(user, pass, driverClass, unauthedUrl, fullUrl)
    }

    private fun urlDecode(str: String?): String? =
            if (str == null) null else URLDecoder.decode(str, Charsets.UTF_8)

    override fun getConnectionUrlFromAlias(alias: String): Optional<String> = emptyOptional() // TODO

    /**
     * Create a new ConnectionInfo with the given parameters.
     *
     * @property user The username to use when connecting to the database
     * @property password The password to connect with. If user is not null, password must not be null
     * @property driverClassName The class name of the driver to use for this connection
     * @property authlessUrl A JDBC url for this driver not containing authentication information
     * @property fullUrl The full jdbc url containing user, password, and database info
     */
    private data class ConnectionInfo(
            val user: String?,
            val password: String?,
            val driverClassName: String,
            val authlessUrl: String,
            val fullUrl: String
    )
}
