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
import org.spongepowered.api.item.inventory.type.CarriedInventory;

import java.util.Optional;

/**
 * All {@link CarriedInventory}s should implement this
 * interface to auto implement the carrier methods.
 *
 * @param <C> The carrier type
 */
@SuppressWarnings("unchecked")
public interface ICarriedInventory<C extends Carrier> extends CarriedInventory<C> {

    @Override
    default Optional<C> getCarrier() {
        final CarrierReference<C> ref = ((AbstractInventory) this).carrierReference;
        return ref == null ? Optional.empty() : ref.get();
    }

    default <R> Optional<R> getCarrierAs(Class<R> ret) {
        final CarrierReference<C> ref = ((AbstractInventory) this).carrierReference;
        return ref == null ? Optional.empty() : ref.as(ret);
    }
}
