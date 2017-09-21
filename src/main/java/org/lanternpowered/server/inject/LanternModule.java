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
package org.lanternpowered.server.inject;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Exposed;
import com.google.inject.Inject;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import joptsimple.OptionParser;
import org.apache.logging.log4j.LogManager;
import org.lanternpowered.server.LanternServer;
import org.lanternpowered.server.asset.AssetRepository;
import org.lanternpowered.server.asset.LanternAssetManager;
import org.lanternpowered.server.asset.json.AssetRepositoryJsonDeserializer;
import org.lanternpowered.server.command.LanternCommandDisambiguator;
import org.lanternpowered.server.command.LanternCommandManager;
import org.lanternpowered.server.config.GlobalConfig;
import org.lanternpowered.server.config.LanternConfigManager;
import org.lanternpowered.server.data.LanternDataManager;
import org.lanternpowered.server.data.property.LanternPropertyRegistry;
import org.lanternpowered.server.event.LanternCauseStackManager;
import org.lanternpowered.server.event.LanternEventManager;
import org.lanternpowered.server.game.DirectoryKeys;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternGameRegistry;
import org.lanternpowered.server.game.LanternPlatform;
import org.lanternpowered.server.game.version.LanternMinecraftVersion;
import org.lanternpowered.server.inject.config.ConfigDirAnnotation;
import org.lanternpowered.server.inject.option.OptionModule;
import org.lanternpowered.server.inject.provider.ChannelBindingProvider;
import org.lanternpowered.server.inject.provider.NamedLog4jLoggerProvider;
import org.lanternpowered.server.inject.provider.NamedSlf4jLoggerProvider;
import org.lanternpowered.server.inject.provider.PluginAssetProvider;
import org.lanternpowered.server.inject.provider.PluginContainerProvider;
import org.lanternpowered.server.inject.provider.ServiceObjectProvider;
import org.lanternpowered.server.inject.provider.SpongeExecutorServiceProvider;
import org.lanternpowered.server.network.channel.LanternChannelRegistrar;
import org.lanternpowered.server.plugin.InternalPluginContainer;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.lanternpowered.server.plugin.LanternPluginManager;
import org.lanternpowered.server.profile.LanternGameProfileManager;
import org.lanternpowered.server.scheduler.LanternScheduler;
import org.lanternpowered.server.service.LanternServiceManager;
import org.lanternpowered.server.util.PathUtils;
import org.lanternpowered.server.world.LanternTeleportHelper;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetId;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.property.PropertyRegistry;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelId;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.profile.GameProfileManager;
import org.spongepowered.api.scheduler.AsynchronousExecutor;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.SynchronousExecutor;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.rcon.RconService;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.world.TeleportHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nullable;

public class LanternModule extends PrivateModule {

    private static abstract class SharedConfigDir<T> implements Provider<T> {

        @Inject @Named(DirectoryKeys.CONFIG) Path configDir;
    }

    private static class SharedConfigDirAsPath extends SharedConfigDir<Path> {
        @Override
        public Path get() {
            return this.configDir;
        }
    }

    private static class SharedConfigDirAsFile extends SharedConfigDir<File> {
        @Override
        public File get() {
            return this.configDir.toFile();
        }
    }

    private final Logger logger;
    private final String[] arguments;
    private final OptionParser optionParser;

    public LanternModule(Logger logger, String[] arguments, OptionParser optionParser) {
        this.optionParser = optionParser;
        this.arguments = arguments;
        this.logger = logger;
    }

    @Override
    protected void configure() {
        // Inject into sponge
        requestStaticInjection(Sponge.class);

        // Injection Points
        install(new InjectionPointProvider());

        // The logger
        bind(Logger.class)
                .toInstance(this.logger);

        // Options
        install(new OptionModule() {
            @Override
            protected void configure0() {
                // Bind the arguments
                bindArguments().toInstance(arguments);
                // Bind the parser
                bindParser().toInstance(optionParser);
                // Expose the Option annotation
                exposeOptions();
            }
        });

        // Directories: Plugins, Libraries, Configs, ...
        bindAndExposePath(DirectoryKeys.ROOT, DirectoryKeys.DefaultValues.ROOT);
        // See below for all the other ones

        // Basic Configuration Annotations
        bindAndExpose(Path.class, ConfigDirAnnotation.SHARED)
                .toProvider(SharedConfigDirAsPath.class).in(Scopes.SINGLETON);
        bindAndExpose(File.class, ConfigDirAnnotation.SHARED)
                .toProvider(SharedConfigDirAsFile.class).in(Scopes.SINGLETON);

        // The Game
        bindAndExpose(Server.class)
                .to(LanternServer.class);
        bindAndExpose(LanternServer.class);
        bindAndExpose(Game.class)
                .to(LanternGame.class);
        bindAndExpose(MinecraftVersion.class)
                .toInstance(LanternMinecraftVersion.CURRENT);
        bindAndExpose(ServiceManager.class)
                .to(LanternServiceManager.class);
        bindAndExpose(AssetManager.class)
                .to(LanternAssetManager.class);
        bindAndExpose(GameRegistry.class)
                .to(LanternGameRegistry.class);
        bindAndExpose(TeleportHelper.class)
                .to(LanternTeleportHelper.class);
        bindAndExpose(Scheduler.class)
                .to(LanternScheduler.class);
        bindAndExpose(CommandManager.class)
                .to(LanternCommandManager.class);
        bindAndExpose(Platform.class)
                .to(LanternPlatform.class);
        bindAndExpose(EventManager.class)
                .to(LanternEventManager.class);
        bindAndExpose(ChannelRegistrar.class)
                .to(LanternChannelRegistrar.class);
        bindAndExpose(GameProfileManager.class)
                .to(LanternGameProfileManager.class);
        bindAndExpose(PropertyRegistry.class)
                .to(LanternPropertyRegistry.class);
        bindAndExpose(ConfigManager.class)
                .to(LanternConfigManager.class);
        bindAndExpose(PluginManager.class)
                .to(LanternPluginManager.class);
        bindAndExpose(DataManager.class)
                .to(LanternDataManager.class);
        bindAndExpose(PropertyRegistry.class)
                .to(LanternPropertyRegistry.class);
        bindAndExpose(CauseStackManager.class)
                .to(LanternCauseStackManager.class);

        // Services
        bindService(PermissionService.class);
        bindService(BanService.class);
        bindService(WhitelistService.class);
        bindService(UserStorageService.class);
        bindService(SqlService.class);
        bindService(PaginationService.class);
        bindService(RconService.class);

        // The Indexed Channel Binding
        bindAndExpose(ChannelBinding.IndexedMessageChannel.class)
                .toProvider(ChannelBindingProvider.Indexed.class);
        bindAndExpose(ChannelBinding.IndexedMessageChannel.class, Named.class)
                .toProvider(ChannelBindingProvider.Indexed.class);
        bindAndExpose(ChannelBinding.IndexedMessageChannel.class, ChannelId.class)
                .toProvider(ChannelBindingProvider.Indexed.class);
        // The Raw Channel Binding
        bindAndExpose(ChannelBinding.RawDataChannel.class)
                .toProvider(ChannelBindingProvider.Raw.class);
        bindAndExpose(ChannelBinding.RawDataChannel.class, Named.class)
                .toProvider(ChannelBindingProvider.Raw.class);
        bindAndExpose(ChannelBinding.RawDataChannel.class, ChannelId.class)
                .toProvider(ChannelBindingProvider.Raw.class);

        // Assets
        bindAndExpose(Asset.class, Named.class)
                .toProvider(PluginAssetProvider.class);
        bindAndExpose(Asset.class, AssetId.class)
                .toProvider(PluginAssetProvider.class);

        // Internal Plugin Containers
        bindAndExpose(PluginContainer.class, Names.named(InternalPluginsInfo.Api.IDENTIFIER))
                .to(InternalPluginContainer.Api.class);
        bindAndExpose(PluginContainer.class, Names.named(InternalPluginsInfo.SpongePlatform.IDENTIFIER))
                .to(InternalPluginContainer.SpongePlatform.class);
        bindAndExpose(PluginContainer.class, Names.named(InternalPluginsInfo.Minecraft.IDENTIFIER))
                .to(InternalPluginContainer.Minecraft.class);
        bindAndExpose(PluginContainer.class, Names.named(InternalPluginsInfo.Implementation.IDENTIFIER))
                .to(InternalPluginContainer.Implementation.class);

        // Loggers
        bindAndExpose(Logger.class, Named.class)
                .toProvider(NamedSlf4jLoggerProvider.class);
        expose(org.apache.logging.log4j.Logger.class);
        bindAndExpose(org.apache.logging.log4j.Logger.class, Named.class)
                .toProvider(NamedLog4jLoggerProvider.class);

        // Other Plugin Containers
        bindAndExpose(PluginContainer.class, Named.class)
                .toProvider(PluginContainerProvider.class);

        // Sponge Executor Services
        bindAndExpose(SpongeExecutorService.class, SynchronousExecutor.class)
                .toProvider(SpongeExecutorServiceProvider.Synchronous.class);
        bindAndExpose(SpongeExecutorService.class, AsynchronousExecutor.class)
                .toProvider(SpongeExecutorServiceProvider.Asynchronous.class);
    }

    private <T> void bindService(Class<T> clazz) {
        final TypeToken<Service<T>> token = new TypeToken<Service<T>>() {}
                .where(new TypeParameter<T>() {}, clazz);
        final TypeLiteral literal = TypeLiteral.get(token.getType());
        //noinspection unchecked
        bindAndExpose(literal).toProvider(ServiceObjectProvider.class);
    }

    private <T> LinkedBindingBuilder<T> bindAndExpose(Class<T> clazz, Annotation annotation) {
        final LinkedBindingBuilder<T> builder = bind(clazz).annotatedWith(annotation);
        expose(clazz).annotatedWith(annotation);
        return builder;
    }

    private <T> LinkedBindingBuilder<T> bindAndExpose(Class<T> clazz, Class<? extends Annotation> annotationType) {
        final LinkedBindingBuilder<T> builder = bind(clazz).annotatedWith(annotationType);
        expose(clazz).annotatedWith(annotationType);
        return builder;
    }

    private <T> LinkedBindingBuilder<T> bindAndExpose(Class<T> clazz) {
        final LinkedBindingBuilder<T> builder = bind(clazz);
        expose(clazz);
        return builder;
    }

    private <T> LinkedBindingBuilder<T> bindAndExpose(TypeLiteral<T> clazz) {
        final LinkedBindingBuilder<T> builder = bind(clazz);
        expose(clazz);
        return builder;
    }

    private void bindAndExposePath(String name, Path path) {
        bindPath(name, path);
        expose(Path.class).annotatedWith(Names.named(name));
        expose(File.class).annotatedWith(Names.named(name));
    }

    private void bindPath(String name, Path path) {
        bind(Path.class).annotatedWith(Names.named(name)).toInstance(path);
        bind(File.class).annotatedWith(Names.named(name)).toInstance(path.toFile());
    }

    @Provides
    @Singleton
    private PluginContainer providePluginContainer(@Named(InternalPluginsInfo.Implementation.IDENTIFIER) PluginContainer plugin) {
        return plugin;
    }

    @Exposed
    @Provides
    @Singleton
    private org.apache.logging.log4j.Logger provideSlf4jAsLog4jLogger(Logger logger) {
        return LogManager.getLogger(logger.getName());
    }

    @Exposed
    @Provides
    @Singleton
    private LanternAssetManager provideAssetManager(@Option({ "asset-repository-config", "asset-repo-config" })
            @Nullable Path repoConfig, LanternPluginManager pluginManager) {
        final Gson gson = new GsonBuilder().registerTypeAdapter(AssetRepository.class,
                new AssetRepositoryJsonDeserializer(pluginManager)).create();
        try {
            URL url;
            if (repoConfig != null) {
                url = PathUtils.toURL(repoConfig);
            } else {
                url = getClass().getClassLoader().getResource("assets_repo.json");
                checkNotNull(url);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                return new LanternAssetManager(gson.fromJson(reader, AssetRepository.class));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Exposed
    @Provides
    @Singleton
    private LanternCommandManager provideCommandManager(Logger logger, LanternGame game) {
        return new LanternCommandManager(logger, new LanternCommandDisambiguator(game));
    }

    //////////////////////////
    /// Directory bindings ///
    //////////////////////////

    @Exposed
    @Named(DirectoryKeys.CONFIG)
    @Provides
    @Singleton
    private Path provideConfigDirectory(@Option({ "config-directory", "config-dir" }) @Nullable Path configDir) {
        return configDir == null ? DirectoryKeys.DefaultValues.CONFIG : configDir;
    }

    @Exposed
    @Named(DirectoryKeys.CONFIG)
    @Provides
    @Singleton
    private File provideConfigDirectoryAsFile(@Named(DirectoryKeys.CONFIG) Path configDir) {
        return configDir.toFile();
    }

    @Exposed
    @Named(DirectoryKeys.PLUGINS)
    @Provides
    @Singleton
    private Path providePluginsDirectory(@Option({ "plugins-directory", "plugins-dir" }) @Nullable Path pluginsDir) {
        return pluginsDir == null ? DirectoryKeys.DefaultValues.PLUGINS : pluginsDir;
    }

    @Exposed
    @Named(DirectoryKeys.PLUGINS)
    @Provides
    @Singleton
    private File providePluginsDirectoryAsFile(@Named(DirectoryKeys.PLUGINS) Path pluginsDir) {
        return pluginsDir.toFile();
    }

    @Exposed
    @Named(DirectoryKeys.ROOT_WORLD)
    @Provides
    @Singleton
    private Path provideRootWorldDirectory(@Named(DirectoryKeys.ROOT) Path rootDir, GlobalConfig globalConfig) {
        return rootDir.resolve(globalConfig.getRootWorldFolder());
    }

    @Exposed
    @Named(DirectoryKeys.ROOT_WORLD)
    @Provides
    @Singleton
    private File provideRootWorldDirectoryAsFile(@Named(DirectoryKeys.ROOT_WORLD) Path rootWorldDir) {
        return rootWorldDir.toFile();
    }

    //////////////////////
    /// Helper Methods ///
    //////////////////////

    // TODO: Move this?
    private static InputStream extractAndGet(Path rootDir, String path, String targetDir) throws IOException {
        final Path dir = rootDir.resolve(targetDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        final Path file = dir.resolve(path);
        if (!Files.exists(file)) {
            final InputStream is = LanternGame.class.getResourceAsStream('/' + path);
            if (is == null) {
                throw new IllegalArgumentException("The resource \"" + path + "\" doesn't exist.");
            }
            try {
                Files.copy(is, file);
            } finally {
                is.close();
            }
        }
        return Files.newInputStream(file);
    }
}
