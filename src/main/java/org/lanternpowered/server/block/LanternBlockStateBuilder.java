package org.lanternpowered.server.block;

import java.util.Collection;
import java.util.List;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockStateBuilder;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.service.persistence.InvalidDataException;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class LanternBlockStateBuilder implements BlockStateBuilder {

    private List<DataManipulator<?,?>> manipulators = Lists.newArrayList();
    private BlockType blockType;

    @Override
    public <M extends DataManipulator<M, ?>> BlockStateBuilder add(M manipulator) {
        this.manipulators.add((DataManipulator<?, ?>) manipulator);
        return this;
    }

    @Override
    public <I extends ImmutableDataManipulator<I, ?>> BlockStateBuilder add(I manipulator) {
        this.manipulators.add((DataManipulator<?, ?>) manipulator);
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public BlockStateBuilder from(BlockState holder) {
        this.blockType = holder.getType();
        this.manipulators.clear();
        this.manipulators.addAll((Collection) holder.getManipulators());
        return this;
    }

    @Override
    public BlockStateBuilder reset() {
        this.manipulators.clear();
        this.blockType = null;
        return this;
    }

    @Override
    public BlockState build() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<BlockState> build(DataView container) throws InvalidDataException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockStateBuilder blockType(BlockType blockType) {
        this.blockType = blockType;
        this.manipulators.clear();
        return this;
    }
}
