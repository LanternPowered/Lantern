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
package org.lanternpowered.server.network.block.vanilla

import org.lanternpowered.api.util.index.requireKey
import org.lanternpowered.server.network.block.VanillaBlockEntityProtocol
import org.lanternpowered.server.registry.type.data.BannerPatternInternalStringIdIndex
import org.lanternpowered.server.registry.type.data.DyeColorRegistry
import org.spongepowered.api.block.entity.BlockEntity
import org.spongepowered.api.data.Keys
import org.spongepowered.api.data.meta.BannerPatternLayer
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.type.DyeColor
import org.spongepowered.api.data.type.DyeColors

class BannerBlockEntityProtocol<T : BlockEntity>(tile: T) : VanillaBlockEntityProtocol<T>(tile) {

    private var lastPatternLayers: List<BannerPatternLayer>? = null
    private var lastBaseColor: DyeColor? = null

    override val type: String
        get() = "minecraft:banner"

    override fun populateInitData(dataView: DataView) {
        val dyeColor = this.blockEntity.get(Keys.DYE_COLOR).orElseGet(DyeColors.WHITE)
        val patternLayers = this.blockEntity.get(Keys.BANNER_PATTERN_LAYERS).orElse(emptyList())
        update(dataView, dyeColor, patternLayers)
    }

    override fun populateUpdateData(dataViewSupplier: () -> DataView) {
        val baseColor = this.blockEntity.get(Keys.DYE_COLOR).orElseGet(DyeColors.WHITE)
        val patternLayers = this.blockEntity.get(Keys.BANNER_PATTERN_LAYERS).orElse(emptyList())
        if (baseColor != this.lastBaseColor ||
                patternLayers != this.lastPatternLayers) {
            update(dataViewSupplier(), baseColor, patternLayers)
            this.lastBaseColor = baseColor
            this.lastPatternLayers = patternLayers
        }
    }

    companion object {

        private val baseColorQuery = DataQuery.of("Base")
        private val layersQuery = DataQuery.of("Patterns")
        private val layerIdQuery = DataQuery.of("Pattern")
        private val layerColorQuery = DataQuery.of("Color")

        private fun update(dataView: DataView, baseColor: DyeColor, patternLayers: List<BannerPatternLayer>) {
            dataView[this.baseColorQuery] = 15 - DyeColorRegistry.getId(baseColor)
            if (patternLayers.isEmpty())
                return
            dataView[this.layersQuery] = patternLayers.map { layer ->
                DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
                        .set(this.layerIdQuery, BannerPatternInternalStringIdIndex.requireKey(layer.shape))
                        .set(this.layerColorQuery, 15 - DyeColorRegistry.getId(layer.color))
            }
        }
    }
}
