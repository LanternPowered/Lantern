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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.inventory.Carrier;

import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

@FunctionalInterface
public interface CarrierBasedTransformer<R extends AbstractInventory, B extends AbstractArchetypeBuilder<R, ? super R, B>> {

    /**
     * Attempts to generate/load a {@link LanternInventoryArchetype} for the
     * given {@link Carrier}. A {@link Supplier} is applied to retrieve a
     * copy of the {@link AbstractBuilder} that this transformer is applied to.
     * It is recommend to cache the {@link LanternInventoryArchetype} and only
     * get a new {@link AbstractBuilder} instance when creating a new variant.
     * Returning {@code null} can be used to fallback to the default
     * {@link LanternInventoryArchetype}.
     *
     * @param carrier The carrier
     * @param builderSupplier The builder copy supplier
     * @return The inventory archetype, or {@code null} when falling back to default
     */
    @Nullable
    default LanternInventoryArchetype<R> apply(Carrier carrier, Supplier<B> builderSupplier) {
        return apply(carrier.getClass(), builderSupplier);
    }

    /**
     * Attempts to generate/load a {@link LanternInventoryArchetype} for the
     * given {@link Carrier} type. A {@link Supplier} is applied to retrieve a
     * copy of the {@link AbstractBuilder} that this transformer is applied to.
     * It is recommend to cache the {@link LanternInventoryArchetype} and only
     * get a new {@link AbstractBuilder} instance when creating a new variant.
     * Returning {@code null} can be used to fallback to the default
     * {@link LanternInventoryArchetype}.
     *
     * @param carrierType The carrier type
     * @param builderSupplier The builder copy supplier
     * @return The inventory archetype, or {@code null} when falling back to default
     */
    @Nullable
    LanternInventoryArchetype<R> apply(Class<? extends Carrier> carrierType, Supplier<B> builderSupplier);

}
