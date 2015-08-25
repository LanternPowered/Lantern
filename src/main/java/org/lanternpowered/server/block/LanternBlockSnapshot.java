package org.lanternpowered.server.block;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;

import com.flowpowered.math.vector.Vector3i;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.spongepowered.api.data.DataQuery.of;

public class LanternBlockSnapshot implements BlockSnapshot {

    private Vector3i location;
    private BlockState state;

    public LanternBlockSnapshot(Vector3i location, BlockState blockState) {
        this.location = checkNotNull(location, "location");
        this.state = checkNotNull(blockState, "blockState");
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = new MemoryDataContainer();
        container.set(of("x"), this.location.getX());
        container.set(of("y"), this.location.getY());
        container.set(of("z"), this.location.getZ());
        container.set(of("state"), this.state);
        return container;
    }

    @Override
    public BlockState getState() {
        return this.state;
    }

    @Override
    public void setBlockState(BlockState blockState) {
        this.state = checkNotNull(blockState, "blockState");
    }

    @Override
    public Vector3i getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(Vector3i location) {
        this.location = checkNotNull(location, "location");
    }

    @Override
    public LanternBlockSnapshot copy() {
        return new LanternBlockSnapshot(this.location, this.state);
    }

}
