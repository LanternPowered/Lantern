/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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

import java.util.Optional;

import org.spongepowered.api.Game;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.util.persistence.DataBuilder;
import org.spongepowered.api.util.persistence.InvalidDataException;

@NonnullByDefault
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
    public DataContainer toContainer() {
        return new MemoryDataContainer().set(BANNER_SHAPE, this.shape.getId()).set(DYE_COLOR, this.color.getName());
    }

    public static class Builder implements DataBuilder<PatternLayer> {

        private final Game game;

        public Builder(Game game) {
            this.game = game;
        }

        @Override
        public Optional<PatternLayer> build(DataView container) throws InvalidDataException {
            String bannerShape = container.getString(BANNER_SHAPE).orElse(null);
            String dyeColor = container.getString(DYE_COLOR).orElse(null);
            if (bannerShape == null || dyeColor == null) {
                return Optional.empty();
            }
            BannerPatternShape shape = this.game.getRegistry().getType(BannerPatternShape.class, bannerShape).orElse(null);
            DyeColor color = this.game.getRegistry().getType(DyeColor.class, dyeColor).orElse(null);
            if (shape == null || color == null) {
                return Optional.empty();
            }
            return Optional.of(new LanternPatternLayer(shape, color));
        }
    }

}
