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
package org.lanternpowered.server.service.sql;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.service.CloseableService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.sql.SqlService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.sql.DataSource;

/**
 * Implementation of a SQL-using service.
 *
 * <p>This implementation does a few interesting things<br>
 *     - It's thread-safe
 *     - It allows applying additional driver-specific connection
 *     properties -- this allows us to do some light performance tuning in
 *     cases where we don't want to be as conservative as the driver developers
 *     - Caches DataSources. This cache is currently never cleared of stale entries
 *     -- if some plugin makes database connections to a ton of different databases
 *     we may want to implement this, but it is kinda unimportant.
 */
public class LanternSqlService implements SqlService, CloseableService {

    private static final Map<String, Properties> PROTOCOL_SPECIFIC_PROPS;
    private static final Map<String, BiFunction<PluginContainer, String, String>> PATH_CANONICALIZERS;

    static {
        final ImmutableMap.Builder<String, Properties> build = ImmutableMap.builder();
        final Properties mySqlProps = new Properties();
        // Config options based on:
        // http://assets.en.oreilly.com/1/event/21/Connector_J%20Performance%20Gems%20Presentation.pdf
        mySqlProps.setProperty("useConfigs", "maxPerformance");
        build.put("com.mysql.jdbc.Driver", mySqlProps);
        build.put("org.mariadb.jdbc.Driver", mySqlProps);

        PROTOCOL_SPECIFIC_PROPS = build.build();
        PATH_CANONICALIZERS = ImmutableMap.of("h2", (plugin, orig) -> {
            // Bleh if only h2 had a better way of supplying a base directory... oh well...
            final org.h2.engine.ConnectionInfo h2Info = new org.h2.engine.ConnectionInfo(orig);
            if (!h2Info.isPersistent() || h2Info.isRemote()) {
                return orig;
            }
            if (orig.startsWith("file:")) {
                orig = orig.substring("file:".length());
            }
            final Path origPath = Paths.get(orig);
            if (origPath.isAbsolute()) {
                return origPath.toString();
            } else {
                return Lantern.getGame().getConfigManager().getPluginConfig(plugin)
                        .getDirectory().resolve(orig).toAbsolutePath().toString();
            }
        });
    }

    private final LoadingCache<ConnectionInfo, HikariDataSource> connectionCache = Caffeine.newBuilder()
            .removalListener((RemovalListener<ConnectionInfo, HikariDataSource>) (key, value, cause) -> {
                if (value != null) {
                    value.close();
                }
            })
            .build(key -> {
                final HikariConfig config = new HikariConfig();
                config.setUsername(key.getUser());
                config.setPassword(key.getPassword());
                config.setDriverClassName(key.getDriverClassName());
                // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing for info on pool sizing
                config.setMaximumPoolSize((Runtime.getRuntime().availableProcessors() * 2) + 1);
                final Properties driverSpecificProperties = PROTOCOL_SPECIFIC_PROPS.get(key.getDriverClassName());
                if (driverSpecificProperties != null) {
                    config.setDataSourceProperties(driverSpecificProperties);
                }
                config.setJdbcUrl(key.getAuthlessUrl());
                return new HikariDataSource(config);
            });

    @Override
    public DataSource getDataSource(String jdbcConnection) throws SQLException {
        return getDataSource(null, jdbcConnection);
    }

    @Override
    public DataSource getDataSource(@Nullable Object plugin, String jdbcConnection) throws SQLException {
        jdbcConnection = getConnectionUrlFromAlias(jdbcConnection).orElse(jdbcConnection);
        PluginContainer container = null;
        if (plugin != null) {
            container = Sponge.getPluginManager().fromInstance(plugin).orElseThrow(() -> new IllegalArgumentException(
                    "The provided plugin object does not have an associated plugin container"
                            + " (in other words, is 'plugin' actually your plugin object?"));
        }
        final ConnectionInfo info = ConnectionInfo.fromUrl(container, jdbcConnection);
        return this.connectionCache.get(info);
    }

    @Override
    public void close() {
        this.connectionCache.invalidateAll();
    }

    public static class ConnectionInfo {

        private static final Pattern URL_REGEX = Pattern.compile("(?:jdbc:)?([^:]+):(//)?(?:([^:]+)(?::([^@]+))?@)?(.*)");
        @Nullable private final String user;
        @Nullable private final String password;
        private final String driverClassName;
        private final String authlessUrl;
        private final String fullUrl;

        /**
         * Create a new ConnectionInfo with the give parameters
         * @param user The username to use when connecting to the database
         * @param password The password to connect with. If user is not null, password must not be null
         * @param driverClassName The class name of the driver to use for this connection
         * @param authlessUrl A JDBC url for this driver not containing authentication information
         * @param fullUrl The full jdbc url containing user, password, and database info
         */
        public ConnectionInfo(@Nullable String user, @Nullable String password, String driverClassName, String authlessUrl, String fullUrl) {
            this.user = user;
            this.password = password;
            this.driverClassName = driverClassName;
            this.authlessUrl = authlessUrl;
            this.fullUrl = fullUrl;
        }

        @Nullable
        public String getUser() {
            return this.user;
        }

        @Nullable
        public String getPassword() {
            return this.password;
        }

        public String getDriverClassName() {
            return this.driverClassName;
        }

        public String getAuthlessUrl() {
            return this.authlessUrl;
        }

        public String getFullUrl() {
            return this.fullUrl;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final ConnectionInfo that = (ConnectionInfo) o;
            return Objects.equals(this.user, that.user)
                    && Objects.equals(this.password, that.password)
                    && Objects.equals(this.driverClassName, that.driverClassName)
                    && Objects.equals(this.authlessUrl, that.authlessUrl)
                    && Objects.equals(this.fullUrl, that.fullUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.user, this.password, this.driverClassName, this.authlessUrl, this.fullUrl);
        }

        /**
         * Extracts the connection info from a JDBC url with additional authentication information as specified in {@link SqlService}.
         *
         * @param container The plugin to put a path relative to
         * @param fullUrl The full JDBC URL as specified in SqlService
         * @return A constructed ConnectionInfo object using the info from the provided URL
         * @throws SQLException If the driver for the given URL is not present
         */
        static ConnectionInfo fromUrl(@Nullable PluginContainer container, String fullUrl) throws SQLException {
            Matcher match = URL_REGEX.matcher(fullUrl);
            if (!match.matches()) {
                throw new IllegalArgumentException("URL " + fullUrl + " is not a valid JDBC URL");
            }

            final String protocol = match.group(1);
            final boolean hasSlashes = match.group(2) != null;
            final String user = match.group(3);
            final String pass = match.group(4);
            String serverDatabaseSpecifier = match.group(5);
            final BiFunction<PluginContainer, String, String> derelativizer = PATH_CANONICALIZERS.get(protocol);
            if (container != null && derelativizer != null) {
                serverDatabaseSpecifier = derelativizer.apply(container, serverDatabaseSpecifier);
            }
            final String unauthedUrl = "jdbc:" + protocol + (hasSlashes ? "://" : ":") + serverDatabaseSpecifier;
            final String driverClass = DriverManager.getDriver(unauthedUrl).getClass().getCanonicalName();
            return new ConnectionInfo(user, pass, driverClass, unauthedUrl, fullUrl);
        }
    }

    @Override
    public Optional<String> getConnectionUrlFromAlias(String alias) {
        return Optional.empty(); // TODO
    }

}
