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
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.inventory.LanternEmptyCarriedInventory;
import org.spongepowered.api.item.inventory.BlockCarrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.MultiBlockCarrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LanternMultiBlockCarrier<T extends CarriedInventory<?>> extends AbstractCarrier<T> implements MultiBlockCarrier {

    private final Location mainLocation;
    private final Map<Location, BlockCarrier> carriers;

    public LanternMultiBlockCarrier(List<BlockCarrier> carriers) {
        checkState(carriers.size() > 0, "At least one block carrier must be present");
        this.mainLocation = carriers.get(0).getLocation();
        final ImmutableMap.Builder<Location, BlockCarrier> builder = ImmutableMap.builder();
        for (BlockCarrier carrier : carriers) {
            builder.put(carrier.getLocation(), carrier);
        }
        this.carriers = builder.build();
    }

    @Override
    public List<Location> getLocations() {
        return ImmutableList.copyOf(this.carriers.keySet());
    }

    @Override
    public Optional<Inventory> getInventory(Location at) {
        checkNotNull(at, "at");
        final BlockCarrier carrier = this.carriers.get(at);
        return carrier == null ? Optional.empty() : Optional.of(carrier.getInventory());
    }

    @Override
    public Optional<Inventory> getInventory(Location at, Direction from) {
        checkNotNull(at, "at");
        checkNotNull(from, "from");
        final BlockCarrier carrier = this.carriers.get(at);
        return carrier == null ? Optional.empty() : Optional.of(carrier.getInventory(from));
    }

    @Override
    public Location getLocation() {
        return this.mainLocation;
    }

    @Override
    public Inventory getInventory(Direction from) {
        return new LanternEmptyCarriedInventory(this);
    }
}
