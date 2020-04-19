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
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.stream.Collectors;

public class FireworkRocketItemTypeObjectSerializer extends ItemTypeObjectSerializer {

    private static final DataQuery FIREWORKS = DataQuery.of("Fireworks");
    private static final DataQuery EXPLOSIONS = DataQuery.of("Explosions");
    private static final DataQuery FLIGHT_MODIFIER = DataQuery.of("Flight");

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(itemStack, valueContainer, dataView);
        final DataView fireworksView = dataView.createView(FIREWORKS);
        valueContainer.remove(Keys.FIREWORK_EFFECTS).ifPresent(effects -> fireworksView.set(EXPLOSIONS,
                effects.stream().map(FireworkChargeItemTypeObjectSerializer::serializeExplosion).collect(Collectors.toList())));
        valueContainer.remove(Keys.FIREWORK_FLIGHT_MODIFIER).ifPresent(modifier -> fireworksView.set(FLIGHT_MODIFIER, modifier.byteValue()));
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(itemStack, valueContainer, dataView);
        dataView.getView(FIREWORKS).ifPresent(fireworksView -> {
            fireworksView.getViewList(EXPLOSIONS).ifPresent(explosions -> valueContainer.set(Keys.FIREWORK_EFFECTS,
                    explosions.stream().map(FireworkChargeItemTypeObjectSerializer::deserializeExplosion).collect(Collectors.toList())));
            fireworksView.getInt(FLIGHT_MODIFIER).ifPresent(value -> valueContainer.set(Keys.FIREWORK_FLIGHT_MODIFIER, value));
        });
    }
}
