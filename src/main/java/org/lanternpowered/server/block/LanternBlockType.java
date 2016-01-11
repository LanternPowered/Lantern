/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.block;

import com.google.common.collect.Lists;
import org.lanternpowered.server.block.state.LanternBlockStateMap;
import org.lanternpowered.server.catalog.LanternPluginCatalogType;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

public class LanternBlockType extends LanternPluginCatalogType implements BlockType, AbstractPropertyHolder {

    // The block state base which contains all the possible block states
    private final LanternBlockStateMap blockStateBase;
    private final Matter matter;
    private BlockState defaultBlockState;
    private boolean tickRandomly;

    public LanternBlockType(String pluginId, String identifier, Matter matter) {
        this(pluginId, identifier, matter, Lists.newArrayList());
    }

    public LanternBlockType(String pluginId, String identifier, Matter matter, BlockTrait<?>... blockTraits) {
        this(pluginId, identifier, matter, Lists.newArrayList(blockTraits));
    }

    public LanternBlockType(String pluginId, String identifier, Matter matter, Iterable<BlockTrait<?>> blockTraits) {
        super(pluginId, identifier);

        // Create the block state base
        this.blockStateBase = new LanternBlockStateMap(this, blockTraits);
        this.defaultBlockState = this.blockStateBase.getBaseState();
        this.matter = matter;
    }

    protected void setDefaultState(BlockState blockState) {
        this.defaultBlockState = blockState;
    }

    /**
     * Gets the base of the block state.
     *
     * @return the block state base
     */
    public LanternBlockStateMap getBlockStateBase() {
        return this.blockStateBase;
    }

    /**
     * Gets the {@link Matter} of the specified block state, normally it should be
     * always the same, which means that the block state is ignored.
     *
     * @param blockState the block state
     * @return the matter
     */
    public Matter getMatter(BlockState blockState) {
        return this.matter;
    }

    /**
     * Gets the extended state for the specified block state, extra properties provided by surrounding blocks
     * may be applied in this method.
     *
     * @param blockState the block state
     * @param location the location
     * @return the actual state
     */
    public BlockState getExtendedState(BlockState blockState, Location<World> location) {
        return blockState;
    }

    /**
     * Gets the extended state for the specified location, extra properties provided by surrounding blocks
     * may be applied in this method.
     *
     * @param location the location
     * @return the actual state
     */
    public BlockState getExtendedState(Location<World> location) {
        return this.getExtendedState(location.getBlock(), location);
    }

    /**
     * Gets the block state of this type from the target item stack.
     *
     * @param itemStack the item stack
     * @return the block state
     */
    public BlockState getStateFromItemStack(ItemStack itemStack) {
        return this.getDefaultState();
    }

    /**
     * Performs a random tick at the specified location for a specific block state.
     *
     * @param location the location
     * @param blockState the block state
     */
    public void doRandomTickAt(Location<World> location, BlockState blockState) {
    }

    @Override
    public Optional<ItemType> getItem() {
        // TODO Auto-generated method stub
        return Optional.empty();
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
        return this.defaultBlockState;
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

    /**
     * Gets a collection with all the {@link BlockState}s of
     * this block type.
     * 
     * @return the block states
     */
    public Collection<BlockState> getAllStates() {
        return this.blockStateBase.getBlockStates();
    }

}
