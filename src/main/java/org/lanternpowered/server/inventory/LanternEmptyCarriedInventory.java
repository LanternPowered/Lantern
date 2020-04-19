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

public class LanternEmptyCarriedInventory extends LanternEmptyInventory implements CarriedInventory<Carrier> {

    private final Carrier carrier;

    public LanternEmptyCarriedInventory(Carrier carrier) {
        this.carrier = carrier;
    }

    @Override
    public Optional<Carrier> getCarrier() {
        return Optional.of(this.carrier);
    }
}
