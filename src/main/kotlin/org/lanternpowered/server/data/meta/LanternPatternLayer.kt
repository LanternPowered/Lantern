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
