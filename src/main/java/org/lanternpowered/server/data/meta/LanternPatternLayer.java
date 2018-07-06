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
package org.lanternpowered.server.data.meta;

import static com.google.common.base.MoreObjects.toStringHelper;

import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;

import java.util.Optional;

public final class LanternPatternLayer implements PatternLayer {

    private static final DataQuery BANNER_SHAPE = DataQuery.of("BannerShapeId");
    private static final DataQuery DYE_COLOR = DataQuery.of("DyeColor");

    private final BannerPatternShape shape;
    private final DyeColor color;

    public LanternPatternLayer(BannerPatternShape shape, DyeColor color) {
        this.shape = shape;
        this.color = color;
    }

    @Override
    public BannerPatternShape getShape() {
        return this.shape;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew().set(BANNER_SHAPE, this.shape.getKey()).set(DYE_COLOR, this.color.getKey());
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("shape", this.shape.getKey())
                .add("dyeColor", this.color.getKey())
                .toString();
    }

    public static class Builder extends AbstractDataBuilder<PatternLayer> {

        private final Game game;

        public Builder(Game game) {
            super(PatternLayer.class, 1);
            this.game = game;
        }

        @Override
        protected Optional<PatternLayer> buildContent(DataView container) throws InvalidDataException {
            final String bannerShape = container.getString(BANNER_SHAPE).orElse(null);
            final String dyeColor = container.getString(DYE_COLOR).orElse(null);
            if (bannerShape == null || dyeColor == null) {
                return Optional.empty();
            }
            final BannerPatternShape shape = this.game.getRegistry()
                    .getType(BannerPatternShape.class, CatalogKey.resolve(bannerShape)).orElse(null);
            final DyeColor color = this.game.getRegistry()
                    .getType(DyeColor.class, CatalogKey.resolve(dyeColor)).orElse(null);
            if (shape == null || color == null) {
                return Optional.empty();
            }
            return Optional.of(new LanternPatternLayer(shape, color));
        }
    }
}
