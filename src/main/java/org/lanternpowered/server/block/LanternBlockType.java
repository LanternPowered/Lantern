package org.lanternpowered.server.block;

import java.util.Collection;

import org.lanternpowered.server.block.state.BlockStateBase;
import org.lanternpowered.server.catalog.LanternSimpleCatalogType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.item.ItemBlock;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class LanternBlockType extends LanternSimpleCatalogType implements BlockType {

    // The block state base which contains all the possible block states
    private final BlockStateBase blockStateBase;
    private final MaterialType materialType;

    private float emittedLight;

    private boolean tickRandomly;
    private boolean areStatisticsEnabled;

    public LanternBlockType(String identifier, MaterialType materialType) {
        this(identifier, materialType, Lists.newArrayList());
    }

    public LanternBlockType(String identifier, MaterialType materialType, Iterable<BlockTrait<?>> blockTraits) {
        super(identifier);

        // Create the block state base
        this.blockStateBase = new BlockStateBase(this, blockTraits);

        // Sets the material type
        this.materialType = materialType;
    }

    @Override
    public Translation getTranslation() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Uhm, this is strange...
     */
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
    public boolean isLiquid() {
        return this.materialType == MaterialType.LIQUID;
    }

    @Override
    public boolean isSolidCube() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isGaseous() {
        return this.materialType == MaterialType.GAS;
    }

    @Override
    public boolean isAffectedByGravity() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean areStatisticsEnabled() {
        return this.areStatisticsEnabled;
    }

    @Override
    public float getEmittedLight() {
        return this.emittedLight;
    }

    @Override
    public Optional<ItemBlock> getHeldItem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isReplaceable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<BlockTrait<?>> getTraits() {
        return this.blockStateBase.getTraits();
    }

    @Override
    public Optional<BlockTrait<?>> getTrait(String blockTrait) {
        return this.blockStateBase.getTrait(blockTrait);
    }

    public static enum MaterialType {
        GAS,
        LIQUID,
        SOLID,
    }
}
