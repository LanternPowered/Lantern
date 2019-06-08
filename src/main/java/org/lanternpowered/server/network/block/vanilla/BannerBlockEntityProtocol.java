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
package org.lanternpowered.server.network.block.vanilla;

import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.lanternpowered.server.data.type.LanternBannerPatternShape;
import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.network.block.BlockEntityProtocol;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

public class BannerBlockEntityProtocol<T extends LanternBlockEntity> extends BlockEntityProtocol<T> {

    private static final DataQuery baseColorQuery = DataQuery.of("Base");
    private static final DataQuery layersQuery = DataQuery.of("Patterns");
    private static final DataQuery layerIdQuery = DataQuery.of("Pattern");
    private static final DataQuery layerColorQuery = DataQuery.of("Color");

    @Nullable private List<PatternLayer> lastPatternLayers;
    @Nullable private DyeColor lastBaseColor;

    /**
     * Constructs a new {@link BannerBlockEntityProtocol} object.
     *
     * @param tile The blockEntity entity
     */
    public BannerBlockEntityProtocol(T tile) {
        super(tile);
    }

    @Override
    protected String getType() {
        return "minecraft:banner";
    }

    @Override
    protected void populateInitData(DataView dataView) {
        final DyeColor dyeColor = this.blockEntity.get(Keys.BANNER_BASE_COLOR).orElse(DyeColors.WHITE);
        final List<PatternLayer> patternLayers = this.blockEntity.get(Keys.BANNER_PATTERNS).orElse(Collections.emptyList());
        update(dataView, dyeColor, patternLayers);
    }

    @Override
    protected void populateUpdateData(Supplier<DataView> dataViewSupplier) {
        final DyeColor baseColor = this.blockEntity.get(Keys.BANNER_BASE_COLOR).orElse(DyeColors.WHITE);
        final List<PatternLayer> patternLayers = this.blockEntity.get(Keys.BANNER_PATTERNS).orElse(Collections.emptyList());
        if (!baseColor.equals(this.lastBaseColor) ||
                !patternLayers.equals(this.lastPatternLayers)) {
            update(dataViewSupplier.get(), baseColor, patternLayers);
            this.lastBaseColor = baseColor;
            this.lastPatternLayers = patternLayers;
        }
    }

    private static void update(DataView dataView, DyeColor baseColor, List<PatternLayer> patternLayers) {
        dataView.set(baseColorQuery, 15 - ((LanternDyeColor) baseColor).getInternalId());
        if (patternLayers.isEmpty()) {
            return;
        }
        dataView.set(layersQuery, patternLayers.stream()
                .map(layer -> DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
                        .set(layerIdQuery, ((LanternBannerPatternShape) layer.getShape()).getInternalId())
                        .set(layerColorQuery, 15 - ((LanternDyeColor) layer.getColor()).getInternalId()))
                .collect(Collectors.toList()));
    }
}
