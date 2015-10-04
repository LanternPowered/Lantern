package org.lanternpowered.server.block;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockStateBuilder;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.service.persistence.InvalidDataException;

public class LanternBlockStateBuilder implements BlockStateBuilder {

    private BlockState blockState;

    public LanternBlockStateBuilder() {
        this.reset();
    }

    @Override
    public BlockStateBuilder reset() {
        this.blockState = BlockTypes.STONE.getDefaultState();
        return this;
    }

    @Override
    public BlockStateBuilder blockType(BlockType blockType) {
        this.blockState = checkNotNull(blockType, "blockType").getDefaultState();
        return this;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public BlockStateBuilder add(DataManipulator<?, ?> manipulator) {
        return this.add((ImmutableDataManipulator) manipulator.asImmutable());
    }

    @Override
    public BlockStateBuilder add(ImmutableDataManipulator<?, ?> manipulator) {
        final Optional<BlockState> optional = this.blockState.with(manipulator);
        if (optional.isPresent()) {
            this.blockState = optional.get();
        }
        return this;
    }

    @Override
    public BlockStateBuilder from(BlockState holder) {
        this.blockState = checkNotNull(holder, "holder");
        return this;
    }

    @Override
    public BlockState build() {
        return this.blockState;
    }

    @Override
    public Optional<BlockState> build(DataView container) throws InvalidDataException {
        // TODO Auto-generated method stub
        return null;
    }
}
