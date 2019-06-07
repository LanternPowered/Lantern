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
package org.lanternpowered.server.block.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.data.AdditionalContainerCollection;
import org.lanternpowered.server.data.IAdditionalDataHolder;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.property.IStorePropertyHolder;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.block.entity.BlockEntityArchetype;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;
import org.spongepowered.math.vector.Vector3i;

import java.util.Optional;
import java.util.UUID;

public class LanternBlockEntityArchetype implements BlockEntityArchetype, IStorePropertyHolder, IAdditionalDataHolder {

    final LanternBlockEntity blockEntity;

    LanternBlockEntityArchetype(LanternBlockEntity internalBlockEntity) {
        this.blockEntity = internalBlockEntity;
    }

    @Override
    public BlockState getState() {
        return this.blockEntity.getBlock();
    }

    @Override
    public LanternBlockEntityType getBlockEntityType() {
        return this.blockEntity.getType();
    }

    @Override
    public DataContainer getBlockEntityData() {
        return this.blockEntity.toContainer();
    }

    @Override
    public boolean validateRawData(DataView container) {
        return this.blockEntity.validateRawData(container);
    }

    @Override
    public void setRawData(DataView container) throws InvalidDataException {
        this.blockEntity.setRawData(container);
    }

    @Override
    public BlockEntityArchetype copy() {
        return new LanternBlockEntityArchetype(copy(this.blockEntity));
    }

    @Override
    public AdditionalContainerCollection<DataManipulator> getAdditionalContainers() {
        return this.blockEntity.getAdditionalContainers();
    }

    @Override
    public ValueCollection getValueCollection() {
        return this.blockEntity.getValueCollection();
    }

    @Override
    public Optional<BlockEntity> apply(Location location) {
        checkNotNull(location, "location");
        final BlockState locState = location.getBlock();
        final BlockState archetypeState = getState();
        if (locState.getType() != archetypeState.getType()) {
            location.setBlock(archetypeState);
        }
        final Optional<BlockEntity> optBlockEntity = location.getBlockEntity();
        optBlockEntity.ifPresent(tile -> {
            final LanternBlockEntity blockEntity = (LanternBlockEntity) tile;
            blockEntity.copyFromFastNoEvents(this.blockEntity);
        });
        return optBlockEntity;
    }

    @Override
    public BlockSnapshot toSnapshot(Location location) {
        BlockState blockState = this.blockEntity.blockState;
        if (blockState == null) {
            blockState = getBlockEntityType().getDefaultBlock();
        }
        final Vector3i pos = location.getBlockPosition();
        final UUID notifier = location.getWorld().getNotifier(pos).orElse(null);
        final UUID creator = location.getWorld().getCreator(pos).orElse(null);
        return new LanternBlockSnapshot(location, blockState, creator, notifier, copy(this.blockEntity));
    }

    public static LanternBlockEntity copy(LanternBlockEntity blockEntity) {
        final LanternBlockEntity copy = blockEntity.getType().construct();
        copy.copyFromFastNoEvents(blockEntity);
        copy.setBlock(blockEntity.getBlock());
        return copy;
    }
}
