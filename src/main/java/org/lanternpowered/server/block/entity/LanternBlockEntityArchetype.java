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
package org.lanternpowered.server.block.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.block.LanternBlockSnapshot;
import org.lanternpowered.server.data.LocalMutableDataHolder;
import org.lanternpowered.server.data.LocalKeyRegistry;
import org.lanternpowered.server.data.property.PropertyHolderBase;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.block.entity.BlockEntityArchetype;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;
import org.spongepowered.math.vector.Vector3i;

import java.util.Optional;
import java.util.UUID;

public class LanternBlockEntityArchetype implements BlockEntityArchetype, LocalMutableDataHolder {

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
    public LocalKeyRegistry getKeyRegistry() {
        return this.blockEntity.getKeyRegistry();
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
        BlockState blockState = this.blockEntity._block;
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
