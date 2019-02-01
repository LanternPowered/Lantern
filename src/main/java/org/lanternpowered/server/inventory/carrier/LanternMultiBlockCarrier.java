/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.inventory.carrier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.inventory.AbstractCarrier;
import org.lanternpowered.server.inventory.LanternEmptyCarriedInventory;
import org.spongepowered.api.item.inventory.BlockCarrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.MultiBlockCarrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LanternMultiBlockCarrier<T extends CarriedInventory<?>> extends AbstractCarrier<T>
        implements MultiBlockCarrier {

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
