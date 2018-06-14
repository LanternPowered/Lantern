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
package org.lanternpowered.server.data.io.store.item;

import static org.lanternpowered.server.data.DataHelper.getOrCreateView;

import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.type.LanternBannerPatternShape;
import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.game.registry.type.data.BannerPatternShapeRegistryModule;
import org.lanternpowered.server.game.registry.type.data.DyeColorRegistryModule;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.stream.Collectors;

public class BannerItemTypeSerializer extends DataValueItemTypeObjectSerializer<DyeColor> {

    private static final DataQuery blockEntityTagQuery = DataQuery.of("BlockEntityTag");
    // private static final DataQuery baseColorQuery = DataQuery.of("Base");
    private static final DataQuery layersQuery = DataQuery.of("Patterns");
    private static final DataQuery layerIdQuery = DataQuery.of("Pattern");
    private static final DataQuery layerColorQuery = DataQuery.of("Color");

    public BannerItemTypeSerializer() {
        super(Keys.BANNER_BASE_COLOR, DyeColorRegistryModule.get(), dataValue -> 15 - dataValue, internalId -> 15 - internalId);
    }

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(itemStack, valueContainer, dataView);
        final DataView blockEntityView = getOrCreateView(dataView, blockEntityTagQuery);
        /*
        valueContainer.remove(Keys.BANNER_BASE_COLOR).ifPresent(baseColor ->
                blockEntityView.set(baseColorQuery, 15 - ((LanternDyeColor) baseColor).getInternalId()));
        */
        valueContainer.remove(Keys.BANNER_PATTERNS).ifPresent(patternLayers ->
                blockEntityView.set(layersQuery, patternLayers.stream()
                        .map(patternLayer -> DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
                                    .set(layerIdQuery, ((LanternBannerPatternShape) patternLayer.getShape()).getInternalId())
                                    .set(layerColorQuery, ((LanternDyeColor) patternLayer.getColor()).getInternalId()))
                        .collect(Collectors.toList())));
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(itemStack, valueContainer, dataView);
        dataView.getView(blockEntityTagQuery).ifPresent(blockEntityView -> {
            /*
            blockEntityView.getInt(baseColorQuery).ifPresent(value ->
                    valueContainer.set(Keys.BANNER_BASE_COLOR, DyeColorRegistryModule.get().getByInternalId(15 - value).get()));
            */
            blockEntityView.getViewList(layersQuery).ifPresent(value ->
                    valueContainer.set(Keys.BANNER_PATTERNS, value.stream()
                            .map(patternView -> {
                                final DyeColor dyeColor = DyeColorRegistryModule.get()
                                        .getByInternalId(15 - patternView.getInt(layerColorQuery).get()).get();
                                final BannerPatternShape shape = BannerPatternShapeRegistryModule.get()
                                        .getByInternalId(patternView.getString(layerIdQuery).get()).get();
                                return PatternLayer.of(shape, dyeColor);
                            })
                            .collect(Collectors.toList())));
        });
    }
}
