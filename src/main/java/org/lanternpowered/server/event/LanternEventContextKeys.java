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
package org.lanternpowered.server.event;

import org.lanternpowered.api.cause.entity.health.HealingType;
import org.spongepowered.api.event.EventContextKey;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public final class LanternEventContextKeys {

    public static final EventContextKey<ItemStack> ORIGINAL_ITEM_STACK = createFor("ORIGINAL_ITEM_STACK");

    public static final EventContextKey<ItemStack> REST_ITEM_STACK = createFor("REST_ITEM_STACK");

    public static final EventContextKey<HealingType> HEALING_TYPE = createFor("HEALING_TYPE");

    public static final EventContextKey<Double> BASE_DAMAGE_VALUE = createFor("BASE_DAMAGE_VALUE");

    public static final EventContextKey<Double> ORIGINAL_DAMAGE_VALUE = createFor("ORIGINAL_DAMAGE_VALUE");

    public static final EventContextKey<Double> FINAL_DAMAGE_VALUE = createFor("FINAL_DAMAGE_VALUE");

    @SuppressWarnings("unchecked")
    private static <T> EventContextKey<T> createFor(String id) {
        return DummyObjectProvider.createFor(EventContextKey.class, id);
    }

    private LanternEventContextKeys() {
    }
}
