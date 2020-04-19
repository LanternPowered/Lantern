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
package org.lanternpowered.server.data.io.store.entity;

import org.lanternpowered.server.data.io.store.ObjectSerializerRegistry;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.entity.LanternItem;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

import java.util.Optional;

public class ItemStore extends EntityStore<LanternItem> {

    private static final DataQuery ITEM = DataQuery.of("Item");
    private static final DataQuery PICKUP_DELAY = DataQuery.of("PickupDelay");
    private static final DataQuery LIFESPAN = DataQuery.of("Lifespan");
    private static final DataQuery AGE = DataQuery.of("Age");

    @Override
    public void serializeValues(LanternItem item, SimpleValueContainer valueContainer, DataView dataView) {
        dataView.set(ITEM, ObjectSerializerRegistry.get().get(LanternItemStack.class).get()
                .serialize((LanternItemStack) valueContainer.remove(Keys.REPRESENTED_ITEM).get().createStack()));
        valueContainer.remove(Keys.PICKUP_DELAY).ifPresent(v -> dataView.set(PICKUP_DELAY, v.shortValue()));
        valueContainer.remove(Keys.DESPAWN_DELAY).ifPresent(v -> {
            dataView.set(AGE, (short) 0);
            dataView.set(LIFESPAN, v);
        });
        super.serializeValues(item, valueContainer, dataView);
    }

    @Override
    public void deserializeValues(LanternItem item, SimpleValueContainer valueContainer, DataView dataView) {
        valueContainer.set(Keys.REPRESENTED_ITEM, ObjectSerializerRegistry.get().get(LanternItemStack.class).get()
                .deserialize(dataView.getView(ITEM).get()).createSnapshot());
        valueContainer.set(Keys.PICKUP_DELAY, dataView.getInt(PICKUP_DELAY).orElse(0));
        final Optional<Integer> lifespan = dataView.getInt(LIFESPAN);
        final Optional<Integer> age = dataView.getInt(AGE);
        if (lifespan.isPresent() && age.isPresent()) {
            valueContainer.set(Keys.DESPAWN_DELAY, lifespan.get() - age.get());
        }
        super.deserializeValues(item, valueContainer, dataView);
    }
}
