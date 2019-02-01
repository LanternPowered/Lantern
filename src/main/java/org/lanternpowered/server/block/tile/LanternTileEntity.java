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

import static java.util.Objects.requireNonNull;

import org.lanternpowered.server.data.AdditionalContainerCollection;
import org.lanternpowered.server.data.DataHelper;
import org.lanternpowered.server.data.DataQueries;
import org.lanternpowered.server.data.IAdditionalDataHolder;
import org.lanternpowered.server.data.ValueCollection;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.game.registry.type.block.TileEntityTypeRegistryModule;
import org.lanternpowered.server.network.tile.AbstractTileEntityProtocol;
import org.lanternpowered.server.network.tile.TileEntityProtocolType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityArchetype;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.Function;

import javax.annotation.Nullable;

@SuppressWarnings({"ConstantConditions", "unchecked"})
public abstract class LanternTileEntity implements TileEntity, IAdditionalDataHolder, AbstractPropertyHolder {

    private LanternTileEntityType tileEntityType;
    private final ValueCollection valueCollection = ValueCollection.create();
    private final AdditionalContainerCollection<DataManipulator<?, ?>> additionalContainers = AdditionalContainerCollection.createConcurrent();

    @Nullable private volatile Location location;
    @Nullable volatile BlockState blockState;
    private volatile boolean valid;
    @Nullable private TileEntityProtocolType<?> protocolType;
    @Nullable private AbstractTileEntityProtocol<?> protocol;

    protected LanternTileEntity() {
        registerKeys();
    }

    protected void registerKeys() {
    }

    /**
     * Pulses this {@link LanternTileEntity}.
     */
    public void pulse() {
    }

    @Override
    public ValueCollection getValueCollection() {
        return this.valueCollection;
    }

    @Override
    public AdditionalContainerCollection<DataManipulator<?, ?>> getAdditionalContainers() {
        return this.additionalContainers;
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
    public LanternTileEntityType getType() {
        if (this.tileEntityType == null) {
            // Load the tile entity type, if not provided earlier
            this.tileEntityType = (LanternTileEntityType) TileEntityTypeRegistryModule.get().getByClass(getClass()).orElseThrow(
                    () -> new IllegalStateException("Every entity class should be registered as a EntityType."));
        }
        return this.tileEntityType;
    }

    /**
     * Sets the {@link TileEntityType}.
     *
     * @param tileEntityType The tile entity type
     */
    void setTileEntityType(LanternTileEntityType tileEntityType) {
        this.tileEntityType = tileEntityType;
    }

    @Override
    public TileEntityArchetype createArchetype() {
        return new LanternTileEntityArchetype(LanternTileEntityArchetype.copy(this));
    }

    @Override
    public Location getLocation() {
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
    public DataHolder copy() {
        return this;
    }

    @Override
    public LocatableBlock getLocatableBlock() {
        return LocatableBlock.builder().location(getLocation()).build();
    }

    /**
     * Sets the {@link Location} of this tile entity.
     *
     * @param location The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Sets the {@link BlockState} of this tile entity.
     *
     * @param blockState The block state
     */
    public void setBlock(BlockState blockState) {
        this.blockState = blockState;
        final LanternTileEntityType type = getType();
        if (type.defaultBlock == null) {
            // Should be fine, in 1.13 ...
            type.defaultBlock = blockState.getType().getDefaultState();
        }
    }

    /**
     * Sets the {@link TileEntityProtocolType} of this {@link TileEntity}.
     *
     * @param protocolType The protocol type
     */
    public void setProtocolType(@Nullable TileEntityProtocolType<?> protocolType) {
        this.protocolType = protocolType;
        if (protocolType != null) {
            this.protocol = (AbstractTileEntityProtocol<?>) ((Function)
                    protocolType.getSupplier()).apply(this);
        } else {
            this.protocol = null;
        }
    }

    @Nullable
    public TileEntityProtocolType<?> getProtocolType() {
        return this.protocolType;
    }

    @Nullable
    public AbstractTileEntityProtocol<?> getProtocol() {
        return this.protocol;
    }
}
