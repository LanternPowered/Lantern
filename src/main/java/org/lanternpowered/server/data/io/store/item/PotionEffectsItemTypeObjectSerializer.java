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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.io.store.misc.PotionEffectSerializer;
import org.lanternpowered.server.game.registry.type.effect.PotionTypeRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;

import java.util.Objects;
import java.util.stream.Collectors;

public class PotionEffectsItemTypeObjectSerializer extends ItemTypeObjectSerializer {

    private static final DataQuery COLOR = DataQuery.of("CustomPotionColor");
    private static final DataQuery EFFECTS = DataQuery.of("CustomPotionEffects");
    private static final DataQuery POTION = DataQuery.of("Potion");

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(itemStack, valueContainer, dataView);
        valueContainer.remove(Keys.COLOR).ifPresent(color -> dataView.set(COLOR, color.getRgb()));
        valueContainer.remove(Keys.POTION_EFFECTS).ifPresent(effects -> {
            if (effects.isEmpty()) {
                return;
            }
            dataView.set(EFFECTS, effects.stream().map(PotionEffectSerializer::serialize).collect(Collectors.toList()));
        });
        valueContainer.remove(Keys.POTION_TYPE).ifPresent(potionType -> dataView.set(POTION, potionType.getKey()));
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(itemStack, valueContainer, dataView);
        dataView.getInt(COLOR).ifPresent(value -> valueContainer.set(Keys.COLOR, Color.ofRgb(value)));
        dataView.getViewList(EFFECTS).ifPresent(effects -> {
            if (effects.isEmpty()) {
                return;
            }
            valueContainer.set(Keys.POTION_EFFECTS, effects.stream()
                    .map(PotionEffectSerializer::deserialize)
                    .filter(Objects::nonNull)
                    .collect(ImmutableList.toImmutableList()));
        });
        dataView.getString(POTION).ifPresent(id -> PotionTypeRegistryModule.INSTANCE.get(CatalogKey.resolve(id)).ifPresent(
                potionType -> valueContainer.set(Keys.POTION_TYPE, potionType)));
    }
}
