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

import com.google.inject.Inject
import com.google.inject.Provider
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.inject.InjectionPoint
import org.spongepowered.api.asset.Asset
import org.spongepowered.api.asset.AssetId
import org.spongepowered.api.asset.AssetManager
import org.spongepowered.plugin.PluginContainer

internal class PluginAssetProvider @Inject constructor(
        private val plugin: PluginContainer,
        private val assetManager: AssetManager,
        private val point: InjectionPoint
) : Provider<Asset> {

    override fun get(): Asset {
        val name = this.point.getAnnotation(AssetId::class.java)!!.value
        return this.assetManager.getAsset(this.plugin, name).orNull() ?: error("Cannot find asset $name")
    }
}
