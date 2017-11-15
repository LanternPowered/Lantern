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
package org.lanternpowered.server.game.registry.type.extra;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.extra.accessory.Accessory;
import org.lanternpowered.server.extra.accessory.LanternTopHat;
import org.lanternpowered.server.extra.accessory.TopHat;
import org.lanternpowered.server.extra.accessory.TopHats;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.data.DyeColorRegistryModule;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.List;

@RegistrationDependency(DyeColorRegistryModule.class)
public final class AccessoryRegistryModule extends DefaultCatalogRegistryModule<Accessory> {

    public AccessoryRegistryModule() {
        super(TopHats.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternTopHat(CatalogKeys.lantern("black_top_hat"), DyeColors.BLACK));
        register(new LanternTopHat(CatalogKeys.lantern("blue_top_hat"), DyeColors.BLUE));
        register(new LanternTopHat(CatalogKeys.lantern("brown_top_hat"), DyeColors.BROWN));
        register(new LanternTopHat(CatalogKeys.lantern("cyan_top_hat"), DyeColors.CYAN));
        register(new LanternTopHat(CatalogKeys.lantern("gold_top_hat")));
        register(new LanternTopHat(CatalogKeys.lantern("gray_top_hat"), DyeColors.GRAY));
        register(new LanternTopHat(CatalogKeys.lantern("green_top_hat"), DyeColors.GREEN));
        register(new LanternTopHat(CatalogKeys.lantern("iron_top_hat")));
        register(new LanternTopHat(CatalogKeys.lantern("light_blue_top_hat"), DyeColors.LIGHT_BLUE));
        register(new LanternTopHat(CatalogKeys.lantern("lime_top_hat"), DyeColors.LIME));
        register(new LanternTopHat(CatalogKeys.lantern("magenta_top_hat"), DyeColors.MAGENTA));
        register(new LanternTopHat(CatalogKeys.lantern("orange_top_hat"), DyeColors.ORANGE));
        register(new LanternTopHat(CatalogKeys.lantern("pink_top_hat"), DyeColors.PINK));
        register(new LanternTopHat(CatalogKeys.lantern("purple_top_hat"), DyeColors.PURPLE));
        register(new LanternTopHat(CatalogKeys.lantern("red_top_hat"), DyeColors.RED));
        register(new LanternTopHat(CatalogKeys.lantern("light_gray_top_hat"), DyeColors.LIGHT_GRAY));
        register(new LanternTopHat(CatalogKeys.lantern("snow_top_hat")));
        register(new LanternTopHat(CatalogKeys.lantern("stone_top_hat")));
        register(new LanternTopHat(CatalogKeys.lantern("white_top_hat"), DyeColors.WHITE));
        register(new LanternTopHat(CatalogKeys.lantern("wood_top_hat")));
        register(new LanternTopHat(CatalogKeys.lantern("yellow_top_hat"), DyeColors.YELLOW));
    }

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        final ImmutableList.Builder<CatalogMappingData> mappingData = ImmutableList.builder();
        mappingData.addAll(super.getCatalogMappings());
        final ImmutableMap.Builder<String, Accessory> topHatMappings = ImmutableMap.builder();
        getAll().stream()
                .filter(accessory -> accessory instanceof TopHat)
                .forEach(accessory -> topHatMappings.put(accessory.getName().replace("_top_hat", ""), accessory));
        mappingData.add(new CatalogMappingData(TopHats.class, topHatMappings.build()));
        return mappingData.build();
    }
}
