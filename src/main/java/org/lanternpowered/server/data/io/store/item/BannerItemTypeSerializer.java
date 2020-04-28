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
package org.lanternpowered.server.data.io.store.item;

import static org.lanternpowered.server.data.DataHelper.getOrCreateView;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.registry.type.data.BannerPatternShapeRegistry;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.meta.BannerPatternLayer;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.stream.Collectors;

public class BannerItemTypeSerializer extends ItemTypeObjectSerializer {

    private static final DataQuery LAYERS = DataQuery.of("Patterns");
    private static final DataQuery LAYER_ID = DataQuery.of("Pattern");
    private static final DataQuery LAYER_COLOR = DataQuery.of("Color");

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(itemStack, valueContainer, dataView);
        final DataView blockEntityView = getOrCreateView(dataView, BLOCK_ENTITY_TAG);
        valueContainer.remove(Keys.BANNER_PATTERN_LAYERS).ifPresent(patternLayers ->
                blockEntityView.set(LAYERS, patternLayers.stream()
                        .map(patternLayer -> DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED)
                                    .set(LAYER_ID, BannerPatternShapeRegistry.INSTANCE.getInternalId(patternLayer.getShape()))
                                    .set(LAYER_COLOR, ((LanternDyeColor) patternLayer.getColor()).getInternalId()))
                        .collect(Collectors.toList())));
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(itemStack, valueContainer, dataView);
        dataView.getView(BLOCK_ENTITY_TAG).ifPresent(blockEntityView ->
                blockEntityView.getViewList(LAYERS).ifPresent(value ->
                        valueContainer.set(Keys.BANNER_PATTERN_LAYERS, value.stream()
                                .map(patternView -> {
                                    final DyeColor dyeColor = DyeColorRegistryModule.get()
                                            .getByInternalId(15 - patternView.getInt(LAYER_COLOR).get()).get();
                                    @Nullable final BannerPatternShape shape = BannerPatternShapeRegistry.INSTANCE
                                            .getByInternalId(patternView.getString(LAYER_ID).get());
                                    if (shape == null)
                                        return null;
                                    return BannerPatternLayer.of(shape, dyeColor);
                                })
                                .filter(layer -> layer != null)
                                .collect(Collectors.toList()))));
    }
}
