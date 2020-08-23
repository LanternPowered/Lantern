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

import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.server.util.guice.GuiceModule
import org.spongepowered.api.asset.Asset
import org.spongepowered.api.asset.AssetId
import org.spongepowered.api.config.ConfigManager
import java.io.File
import java.nio.file.Path

/**
 * A module which provides bindings for configuration annotations.
 */
internal abstract class PluginGuiceModule(
        private val plugin: PluginContainer,
        private val configManager: ConfigManager
) : GuiceModule() {

    override fun configure() {
        bind<PluginContainer>().toInstance(this.plugin)
        bind<Logger>().toInstance(this.plugin.logger)

        bind<Asset>().annotatedWith(AssetId::class.java)
                .toProvider(PluginAssetProvider::class)

        bind<Path>().annotatedWith(ModuleAnnotations.nonSharedConfigDir)
                .providedBy { this.configManager.getPluginConfig(this.plugin).directory }
        bind<File>().annotatedWith(ModuleAnnotations.nonSharedConfigDir)
                .providedBy { this.configManager.getPluginConfig(this.plugin).directory.toFile() }

        // Plugin-private directory config file
        bind<Path>().annotatedWith(ModuleAnnotations.nonSharedDefaultConfig)
                .providedBy { this.configManager.getPluginConfig(this.plugin).configPath }
        bind<File>().annotatedWith(ModuleAnnotations.nonSharedDefaultConfig)
                .providedBy { this.configManager.getPluginConfig(this.plugin).configPath.toFile() }

        // Shared-directory config file
        // TODO: Uhm, is it a good idea to expose a shared config file?
        bind<Path>().annotatedWith(ModuleAnnotations.sharedDefaultConfig)
                .providedBy { this.configManager.getSharedConfig(this.plugin).configPath }
        bind<File>().annotatedWith(ModuleAnnotations.sharedDefaultConfig)
                .providedBy { this.configManager.getSharedConfig(this.plugin).configPath.toFile() }

        // Loader for shared-directory config file
        // TODO: Uhm, is it a good idea to expose a shared config file?
        bind<ConfigurationLoader<CommentedConfigurationNode>>().annotatedWith(ModuleAnnotations.sharedDefaultConfig)
                .providedBy { this.configManager.getSharedConfig(this.plugin).config }

        // Loader for plugin-private directory config file
        bind<ConfigurationLoader<CommentedConfigurationNode>>().annotatedWith(ModuleAnnotations.nonSharedDefaultConfig)
                .providedBy { this.configManager.getPluginConfig(this.plugin).config }
    }
}
