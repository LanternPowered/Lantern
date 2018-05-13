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

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.data.AdditionalContainerCollection;
import org.lanternpowered.server.data.IAdditionalDataHolder;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.world.WeakWorldReferencedLocation;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityArchetype;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class LanternTileEntityArchetype implements TileEntityArchetype, AbstractPropertyHolder, IAdditionalDataHolder {

    private final LanternTileEntity tileEntity;

    LanternTileEntityArchetype(LanternTileEntity internalTileEntity) {
        this.tileEntity = internalTileEntity;
    }

    @Override
    public BlockState getState() {
        return this.tileEntity.getBlock();
    }

    @Override
    public LanternTileEntityType getTileEntityType() {
        return this.tileEntity.getType();
    }

    @Override
    public DataContainer getTileData() {
        return this.tileEntity.toContainer();
    }

    @Override
    public boolean validateRawData(DataView container) {
        return this.tileEntity.validateRawData(container);
    }

    @Override
    public void setRawData(DataView container) throws InvalidDataException {
        this.tileEntity.setRawData(container);
    }

    @Override
    public TileEntityArchetype copy() {
        return new LanternTileEntityArchetype(copy(this.tileEntity));
    }

    @Override
    public AdditionalContainerCollection<DataManipulator<?, ?>> getAdditionalContainers() {
        return this.tileEntity.getAdditionalContainers();
    }

    @Override
    public ValueCollection getValueCollection() {
        return this.tileEntity.getValueCollection();
    }

    @Override
    public Optional<TileEntity> apply(Location<World> location) {
        checkNotNull(location, "location");
        final LanternTileEntity copy = getTileEntityType().construct();
        copy.copyFromFastNoEvents(this.tileEntity);
        copy.setLocation(location);
        return Optional.of(copy);
    }

    @Override
    public BlockSnapshot toSnapshot(Location<World> location) {
        BlockState blockState = this.tileEntity.blockState;
        if (blockState == null) {
            blockState = getTileEntityType().getDefaultBlock();
        }
        final Vector3i pos = location.getBlockPosition();
        final UUID notifier = location.getExtent().getNotifier(pos).orElse(null);
        final UUID creator = location.getExtent().getCreator(pos).orElse(null);
        return new LanternBlockSnapshot(new WeakWorldReferencedLocation(location), blockState,
                creator, notifier, copy(this.tileEntity));
    }

    public static LanternTileEntity copy(LanternTileEntity tileEntity) {
        final LanternTileEntity copy = tileEntity.getType().construct();
        copy.copyFromFastNoEvents(tileEntity);
        copy.setBlock(tileEntity.getBlock());
        return copy;
    }
}
