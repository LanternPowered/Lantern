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

import static java.util.Objects.requireNonNull;

import org.lanternpowered.server.data.DataHelper;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.LocalMutableDataHolder;
import org.lanternpowered.server.data.LocalKeyRegistry;
import org.lanternpowered.server.data.property.PropertyHolderBase;
import org.lanternpowered.server.game.registry.type.block.BlockEntityTypeRegistryModule;
import org.lanternpowered.server.network.block.BlockEntityProtocol;
import org.lanternpowered.server.network.block.BlockEntityProtocolType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.block.entity.BlockEntityArchetype;
import org.spongepowered.api.block.entity.BlockEntityType;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.persistence.Queries;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.world.ServerLocation;

@SuppressWarnings({"ConstantConditions", "unchecked"})
public abstract class LanternBlockEntity implements BlockEntity, LocalMutableDataHolder {

    private LanternBlockEntityType blockEntityType;
    private final LocalKeyRegistry<? extends LanternBlockEntity> localKeyRegistry = LocalKeyRegistry.of();

    @Nullable private volatile ServerLocation location;
    @Nullable volatile BlockState blockState;
    private volatile boolean valid;
    @Nullable private BlockEntityProtocolType<?> protocolType;
    @Nullable private BlockEntityProtocol<?> protocol;

    protected LanternBlockEntity() {
        registerKeys();
    }

    protected void registerKeys() {
    }

    /**
     * Pulses this {@link LanternBlockEntity}.
     */
    public void pulse() {
    }

    @Override
    public LocalKeyRegistry<? extends LanternBlockEntity> getKeyRegistry() {
        return this.localKeyRegistry;
    }

    @Override
    public boolean validateRawData(DataView dataView) {
        return true;
    }

    @Override
    public void setRawData(DataView dataView) throws InvalidDataException {
        DataHelper.deserializeRawData(dataView, this);
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
    public LanternBlockEntityType getType() {
        if (this.blockEntityType == null) {
            // Load the block entity type, if not provided earlier
            this.blockEntityType = (LanternBlockEntityType) BlockEntityTypeRegistryModule.get().getByClass(getClass()).orElseThrow(
                    () -> new IllegalStateException("Every entity class should be registered as a EntityType."));
        }
        return this.blockEntityType;
    }

    /**
     * Sets the {@link BlockEntityType}.
     *
     * @param blockEntityType The block entity type
     */
    void setBlockEntityType(LanternBlockEntityType blockEntityType) {
        this.blockEntityType = blockEntityType;
    }

    @Override
    public BlockEntityArchetype createArchetype() {
        return new LanternBlockEntityArchetype(LanternBlockEntityArchetype.copy(this));
    }

    @Override
    public ServerLocation getLocation() {
        return requireNonNull(this.location, "The location isn't available.");
    }

    @Override
    public BlockState getBlock() {
        return requireNonNull(this.blockState, "The block state isn't available.");
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        final DataContainer dataContainer = DataContainer.createNew()
                .set(Queries.CONTENT_VERSION, getContentVersion())
                .set(DataQueries.TILE_ENTITY_TYPE, getType())
                .set(DataQueries.POSITION, getLocation().getBlockPosition());
        DataHelper.serializeRawData(dataContainer, this);
        return dataContainer;
    }

    @Override
    public BlockEntityArchetype copy() {
        return null;
    }

    @Override
    public LocatableBlock getLocatableBlock() {
        return LocatableBlock.builder().location(getLocation()).build();
    }

    /**
     * Sets the {@link Location} of this block entity.
     *
     * @param location The location
     */
    public void setLocation(ServerLocation location) {
        this.location = location;
    }

    /**
     * Sets the {@link BlockState} of this block entity.
     *
     * @param blockState The block state
     */
    public void setBlock(BlockState blockState) {
        this.blockState = blockState;
        final LanternBlockEntityType type = getType();
        if (type.defaultBlock == null) {
            // Should be fine, in 1.13 ...
            type.defaultBlock = blockState.getType().getDefaultState();
        }
    }

    /**
     * Sets the {@link BlockEntityProtocolType} of this {@link BlockEntity}.
     *
     * @param protocolType The protocol type
     */
    public void setProtocolType(@Nullable BlockEntityProtocolType<?> protocolType) {
        this.protocolType = protocolType;
        if (protocolType != null) {
            this.protocol = (BlockEntityProtocol<?>) ((Function)
                    protocolType.supplier).apply(this);
        } else {
            this.protocol = null;
        }
    }

    @Nullable
    public BlockEntityProtocolType<?> getProtocolType() {
        return this.protocolType;
    }

    @Nullable
    public BlockEntityProtocol<?> getProtocol() {
        return this.protocol;
    }
}
