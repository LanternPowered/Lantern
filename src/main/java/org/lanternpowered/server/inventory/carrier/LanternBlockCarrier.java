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
package org.lanternpowered.server.inventory.carrier;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.inventory.AbstractCarrier;
import org.lanternpowered.server.inventory.LanternEmptyCarriedInventory;
import org.spongepowered.api.item.inventory.BlockCarrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;

public class LanternBlockCarrier<T extends CarriedInventory<?>> extends AbstractCarrier<T>
        implements Locatable, BlockCarrier {

    private final Location location;

    public LanternBlockCarrier(Location location) {
        checkNotNull(location, "location");
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public Inventory getInventory(Direction from) {
        checkNotNull(from, "from");
        return new LanternEmptyCarriedInventory(this);
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("location", this.location);
    }
}
