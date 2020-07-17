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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.io.store.misc.PotionEffectSerializer;
import org.lanternpowered.server.registry.type.potion.PotionTypeRegistry;
import org.spongepowered.api.ResourceKey;
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
        dataView.getString(POTION).flatMap(id -> PotionTypeRegistry.get().getOptional(ResourceKey.resolve(id)))
                .ifPresent(potionType -> valueContainer.set(Keys.POTION_TYPE, potionType));
    }
}
