package org.lanternpowered.server.block;

import org.lanternpowered.server.catalog.LanternSimpleCatalogType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemBlock;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.base.Optional;

public class LanternBlockType extends LanternSimpleCatalogType implements BlockType {

    private float emittedLight;

    private boolean tickRandomly;

    public LanternBlockType(String identifier) {
        super(identifier);
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
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSolidCube() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isGaseous() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isAffectedByGravity() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean areStatisticsEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public float getEmittedLight() {
        // TODO Auto-generated method stub
        return 0;
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
}
