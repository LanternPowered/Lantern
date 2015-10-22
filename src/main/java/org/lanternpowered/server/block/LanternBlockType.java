/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

import java.util.Collection;
import java.util.Optional;

import org.lanternpowered.server.block.state.LanternBlockStateBase;
import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;
import org.spongepowered.api.text.translation.Translation;

import com.google.common.collect.Lists;

public class LanternBlockType extends SimpleLanternCatalogType implements BlockType {

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
