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
package org.lanternpowered.server.data.meta

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.AbstractDataSerializable
import org.lanternpowered.server.registry.type.data.BannerPatternShapeRegistry
import org.lanternpowered.server.registry.type.data.DyeColorRegistry
import org.lanternpowered.api.namespace.NamespacedKey
import org.spongepowered.api.data.meta.BannerPatternLayer
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.persistence.DataContainer
import org.spongepowered.api.data.persistence.DataQuery
import org.spongepowered.api.data.persistence.DataView
import org.spongepowered.api.data.type.BannerPatternShape
import org.spongepowered.api.data.type.DyeColor
import java.util.*

class LanternPatternLayer(
        private val shape: BannerPatternShape,
        private val color: DyeColor
) : AbstractDataSerializable(), BannerPatternLayer {

    override fun getShape() = this.shape
    override fun getColor() = this.color
    override fun getContentVersion() = 1

    override fun toContainer(): DataContainer = super.toContainer()
            .set(BANNER_SHAPE, this.shape.key)
            .set(DYE_COLOR, this.color.key)

    override fun toString() = ToStringHelper(this)
            .add("shape", this.shape.key)
            .add("dyeColor", this.color.key)
            .toString()

    class Builder : AbstractDataBuilder<BannerPatternLayer>(BannerPatternLayer::class.java, 1) {

        override fun buildContent(container: DataView): Optional<BannerPatternLayer> {
            val bannerShape = container.getString(BANNER_SHAPE).orElse(null)
            val dyeColor = container.getString(DYE_COLOR).orElse(null)
            if (bannerShape == null || dyeColor == null) {
                return Optional.empty()
            }
            val shape = BannerPatternShapeRegistry[NamespacedKey.resolve(bannerShape)]
            val color = DyeColorRegistry[NamespacedKey.resolve(dyeColor)]
            return if (shape == null || color == null) {
                Optional.empty()
            } else Optional.of(LanternPatternLayer(shape, color))
        }
    }

    companion object {

        private val BANNER_SHAPE = DataQuery.of("BannerShapeId")
        private val DYE_COLOR = DataQuery.of("DyeColor")
    }
}
