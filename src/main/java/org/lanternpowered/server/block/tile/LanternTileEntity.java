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
package org.lanternpowered.server.block.tile;

import org.lanternpowered.server.data.AbstractDataHolder;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.game.registry.type.block.TileEntityTypeRegistryModule;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityArchetype;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;

public abstract class LanternTileEntity implements TileEntity, AbstractDataHolder, AbstractPropertyHolder {

    private static boolean bypassEntityTypeLookup;

    private final TileEntityType tileEntityType;
    private final Map<Key<?>, KeyRegistration> rawValueMap = new HashMap<>();
    private volatile Location<World> location;
    private volatile boolean valid;

    protected LanternTileEntity() {
        if (!bypassEntityTypeLookup) {
            this.tileEntityType = TileEntityTypeRegistryModule.get().getByClass(this.getClass()).orElseThrow(
                    () -> new IllegalStateException("Every entity class should be registered as a EntityType."));
        } else {
            //noinspection ConstantConditions
            this.tileEntityType = null;
        }
        registerKeys();
    }

    /**
     * Pulses this {@link LanternTileEntity}.
     */
    public void pulse() {
    }

    @Override
    public boolean validateRawData(DataView dataView) {
        return false;
    }

    @Override
    public void setRawData(DataView dataView) throws InvalidDataException {
    }

    @Override
    public Map<Key<?>, KeyRegistration> getRawValueMap() {
        return this.rawValueMap;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public TileEntityType getType() {
        return this.tileEntityType;
    }

    @Override
    public TileEntityArchetype createArchetype() {
        return null;
    }

    @Override
    public Location<World> getLocation() {
        return this.location;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer dataContainer = AbstractDataHolder.super.toContainer();
        // TODO: Add block position, tile type, etc.
        return dataContainer;
    }

    @Override
    public DataHolder copy() {
        return null;
    }

    /**
     * Sets the {@link Location} of this tile entity.
     *
     * @param location The location
     */
    public void setLocation(Location<World> location) {
        this.location = location;
    }

    @Override
    public LocatableBlock getLocatableBlock() {
        return null;
    }
}
