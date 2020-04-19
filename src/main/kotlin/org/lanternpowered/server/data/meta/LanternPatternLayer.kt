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
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.Game
import org.spongepowered.api.data.meta.BannerPatternLayer
import org.spongepowered.api.data.meta.PatternLayer
import org.spongepowered.api.data.persistence.*
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

    class Builder(private val game: Game) : AbstractDataBuilder<BannerPatternLayer>(BannerPatternLayer::class.java, 1) {

        override fun buildContent(container: DataView): Optional<BannerPatternLayer> {
            val bannerShape = container.getString(BANNER_SHAPE).orElse(null)
            val dyeColor = container.getString(DYE_COLOR).orElse(null)
            if (bannerShape == null || dyeColor == null) {
                return Optional.empty()
            }
            val shape = this.game.registry
                    .getType(BannerPatternShape::class.java, CatalogKey.resolve(bannerShape)).orElse(null)
            val color = this.game.registry
                    .getType(DyeColor::class.java, CatalogKey.resolve(dyeColor)).orElse(null)
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
