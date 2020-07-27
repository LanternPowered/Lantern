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
package org.lanternpowered.server.plugin.inject

import org.lanternpowered.api.Game
import org.lanternpowered.api.MinecraftVersion
import org.lanternpowered.api.Platform
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.plugin.PluginManager
import org.lanternpowered.api.registry.GameRegistry
import org.lanternpowered.api.service.ServiceProvider
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.util.guice.InjectionPointProvider
import org.lanternpowered.server.util.guice.GuiceModule
import java.io.File
import java.nio.file.Path
import org.spongepowered.api.command.manager.CommandManager as SpongeCommandManager
import org.spongepowered.api.sql.SqlManager as SpongeSqlManager
import org.spongepowered.api.config.ConfigManager as SpongeConfigManager
import org.spongepowered.api.asset.AssetManager as SpongeAssetManager
import org.spongepowered.api.data.DataManager as SpongeDataManager
import org.spongepowered.api.event.EventManager as SpongeEventManager
import org.spongepowered.api.network.channel.ChannelRegistry as SpongeChannelRegistry
import org.spongepowered.api.util.metric.MetricsConfigManager as SpongeMetricsConfigManager
import org.spongepowered.api.Game as SpongeGame
import org.spongepowered.api.registry.GameRegistry as SpongeGameRegistry
import org.spongepowered.api.plugin.PluginManager as SpongePluginManager
import org.spongepowered.api.service.ServiceProvider as SpongeServiceProvider

internal class ParentGuiceModule(private val game: LanternGame) : GuiceModule() {

    override fun configure() {
        install(InjectionPointProvider())

        bind<SpongeGame>().toInstance(this.game)
        bind<Game>().toInstance(this.game)
        bind<Platform>().toInstance(this.game.platform)
        bind<MinecraftVersion>().toInstance(this.game.minecraftVersion)
        bind<SpongeAssetManager>().toInstance(this.game.assetManager)
        bind<SpongeGameRegistry>().toInstance(this.game.registry)
        bind<GameRegistry>().toInstance(this.game.registry)
        bind<SpongeChannelRegistry>().toInstance(this.game.channelRegistry)
        bind<SpongeEventManager>().toInstance(this.game.eventManager)
        bind<EventManager>().toInstance(this.game.eventManager)
        bind<SpongePluginManager>().toInstance(this.game.pluginManager)
        bind<PluginManager>().toInstance(this.game.pluginManager)
        bind<SpongeDataManager>().toInstance(this.game.dataManager)
        bind<SpongeConfigManager>().toInstance(this.game.configManager)
        bind<SpongeMetricsConfigManager>().toInstance(this.game.metricsConfigManager)
        bind<SpongeSqlManager>().toInstance(this.game.sqlManager)
        bind<SpongeServiceProvider>().toInstance(this.game.serviceProvider)
        bind<ServiceProvider>().toInstance(this.game.serviceProvider)
        bind<SpongeCommandManager>().toInstance(this.game.commandManager)

        bind<Path>().annotatedWith(ModuleAnnotations.sharedConfigDir).toInstance(this.game.configDirectory)
        bind<File>().annotatedWith(ModuleAnnotations.sharedConfigDir).toInstance(this.game.configDirectory.toFile())
    }
}
