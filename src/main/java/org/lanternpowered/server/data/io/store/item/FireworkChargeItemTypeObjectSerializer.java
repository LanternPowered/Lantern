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

import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.effect.firework.LanternFireworkShape;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.item.FireworkShapeRegistryModule;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

final class FireworkChargeItemTypeObjectSerializer extends ItemTypeObjectSerializer {

    private static final DataQuery EXPLOSION = DataQuery.of("Explosion");
    private static final DataQuery EXTRA_EXPLOSIONS = DataQuery.of("ExtraExplosions"); // Lantern

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(itemStack, valueContainer, dataView);
        valueContainer.remove(Keys.FIREWORK_EFFECTS).ifPresent(effects -> {
            if (!effects.isEmpty()) {
                // Also serialize the extra explosion effects
                if (effects.size() > 1) {
                    dataView.set(EXTRA_EXPLOSIONS, effects.subList(1, effects.size()).stream()
                            .map(FireworkChargeItemTypeObjectSerializer::serializeExplosion).collect(Collectors.toList()));
                }
                dataView.set(EXPLOSION, serializeExplosion(effects.get(0)));
            }
        });
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(itemStack, valueContainer, dataView);
        final Optional<DataView> explosion = dataView.getView(EXPLOSION);
        if (explosion.isPresent()) {
            final List<FireworkEffect> effects = new ArrayList<>();
            effects.add(deserializeExplosion(explosion.get()));
            dataView.getViewList(EXTRA_EXPLOSIONS).ifPresent(explosions -> effects.addAll(
                explosions.stream().map(FireworkChargeItemTypeObjectSerializer::deserializeExplosion).collect(Collectors.toList())));
            valueContainer.set(Keys.FIREWORK_EFFECTS, effects);
        }
    }

    private static final DataQuery FLICKER = DataQuery.of("Flicker");
    private static final DataQuery TRAIL = DataQuery.of("Trail");
    private static final DataQuery TYPE = DataQuery.of("Type");
    private static final DataQuery COLORS = DataQuery.of("Colors");
    private static final DataQuery FADE_COLORS = DataQuery.of("FadeColors");

    static DataView serializeExplosion(FireworkEffect effect) {
        final DataView dataView = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
        dataView.set(FLICKER, (byte) (effect.flickers() ? 1 : 0));
        dataView.set(TRAIL, (byte) (effect.hasTrail() ? 1 : 0));
        dataView.set(TYPE, (byte) ((LanternFireworkShape) effect.getShape()).getInternalId());
        dataView.set(COLORS, effect.getColors().stream().mapToInt(Color::getRgb).toArray());
        dataView.set(FADE_COLORS, effect.getFadeColors().stream().mapToInt(Color::getRgb).toArray());
        return dataView;
    }

    static FireworkEffect deserializeExplosion(DataView dataView) {
        final FireworkEffect.Builder builder = FireworkEffect.builder();
        dataView.getInt(FLICKER).ifPresent(v -> builder.flicker(v > 0));
        dataView.getInt(TRAIL).ifPresent(v -> builder.trail(v > 0));
        final Optional<FireworkShape> shape = FireworkShapeRegistryModule.get().getByInternalId(dataView.getInt(TYPE).get());
        if (!shape.isPresent()) {
            Lantern.getLogger().warn("Deserialized firework explosion data with unknown shape: {}", dataView.getInt(TYPE).get());
        } else {
            builder.shape(shape.get());
        }
        dataView.get(COLORS).ifPresent(array -> {
            final int[] iArray = (int[]) array;
            builder.colors(Arrays.stream(iArray).mapToObj(Color::ofRgb).collect(Collectors.toList()));
        });
        dataView.get(FADE_COLORS).ifPresent(array -> {
            final int[] iArray = (int[]) array;
            builder.fades(Arrays.stream(iArray).mapToObj(Color::ofRgb).collect(Collectors.toList()));
        });
        return builder.build();
    }
}
