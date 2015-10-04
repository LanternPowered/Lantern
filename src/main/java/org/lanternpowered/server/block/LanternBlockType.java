package org.lanternpowered.server.block;

import java.util.Collection;
import java.util.Optional;

import org.lanternpowered.server.block.state.LanternBlockStateBase;
import org.lanternpowered.server.catalog.LanternSimpleCatalogType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.collect.Lists;

public class LanternBlockType extends LanternSimpleCatalogType implements BlockType {

    // The block state base which contains all the possible block states
    final LanternBlockStateBase blockStateBase;
    private boolean tickRandomly;

    public LanternBlockType(String identifier, Matter matter) {
        this(identifier, matter, Lists.newArrayList());
    }

    public LanternBlockType(String identifier, Matter matter, BlockTrait<?>... blockTraits) {
        this(identifier, matter, Lists.newArrayList(blockTraits));
    }

    public LanternBlockType(String identifier, Matter matter, Iterable<BlockTrait<?>> blockTraits) {
        super(identifier);

        // Create the block state base
        this.blockStateBase = new LanternBlockStateBase(this, blockTraits);
    }

    @Override
    public Translation getTranslation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public BlockState getDefaultState() {
        return this.blockStateBase.getDefaultBlockState();
    }

    @Override
    public boolean getTickRandomly() {
        return this.tickRandomly;
    }

    @Override
    public void setTickRandomly(boolean tickRandomly) {
        this.tickRandomly = tickRandomly;
    }

    @Override
    public Collection<BlockTrait<?>> getTraits() {
        return this.blockStateBase.getTraits();
    }

    @Override
    public Optional<BlockTrait<?>> getTrait(String blockTrait) {
        return this.blockStateBase.getTrait(blockTrait);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
        return this.getDefaultState().getProperty(propertyClass);
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        return this.getDefaultState().getApplicableProperties();
    }
}
