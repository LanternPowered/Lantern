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
    final BlockStateBase blockStateBase;
    private final MaterialType materialType;

    private float emittedLight = 0f;

    private boolean fullBlock;
    private boolean replaceable;
    private boolean tickRandomly;
    private boolean areStatisticsEnabled;
    private boolean affectedByGravity;

    public LanternBlockType(String identifier, MaterialType materialType) {
        this(identifier, materialType, Lists.newArrayList());
    }

    public LanternBlockType(String identifier, MaterialType materialType, BlockTrait<?>... blockTraits) {
        this(identifier, materialType, Lists.newArrayList(blockTraits));
    }

    public LanternBlockType(String identifier, MaterialType materialType, Iterable<BlockTrait<?>> blockTraits) {
        super(identifier);

        // Create the block state base
        this.blockStateBase = new BlockStateBase(this, blockTraits);

        // Sets the material type
        this.materialType = materialType;

        // Non solid block should always be replaceable
        this.replaceable = materialType != MaterialType.SOLID;
    }

    public void setFullBlock(boolean full) {
        this.fullBlock = full;
    }

    public void setReplaceable(boolean replaceable) {
        this.replaceable = replaceable;
    }

    public void setAffectedByGravity(boolean affected) {
        this.affectedByGravity = affected;
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
        return this.materialType == MaterialType.SOLID && this.fullBlock;
    }

    @Override
    public boolean isGaseous() {
        return this.materialType == MaterialType.GAS;
    }

    @Override
    public boolean isAffectedByGravity() {
        return this.affectedByGravity;
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
        return this.replaceable;
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
