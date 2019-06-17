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
package org.lanternpowered.server.block;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline;
import org.lanternpowered.server.block.behavior.types.RandomTickBehavior;
import org.lanternpowered.server.block.provider.BlockObjectProvider;
import org.lanternpowered.server.block.provider.property.PropertyProviderCollection;
import org.lanternpowered.server.block.state.LanternBlockStateMap;
import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.lanternpowered.server.data.property.PropertyHolderBase;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.BlockSoundGroup;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.AABB;

import java.util.Collection;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternBlockType extends DefaultCatalogType implements BlockType, PropertyHolderBase {

    /**
     * The property provider collection.
     */
    private PropertyProviderCollection propertyProviderCollection;

    /**
     * The block state map.
     */
    private final LanternBlockStateMap blockStateBase;

    /**
     * The translation provider of this block type.
     */
    private final TranslationProvider translationProvider;

    /**
     * The block behavior pipeline of this block type.
     */
    private final MutableBehaviorPipeline<Behavior> behaviorPipeline;

    @Nullable private final BlockEntityProvider tileEntityProvider;

    /**
     * The default block state of this block type.
     */
    private BlockState defaultBlockState;

    @Nullable private ItemType itemType;

    /**
     * Whether this block should tick randomly.
     */
    private boolean tickRandomly;

    @Nullable private BlockObjectProvider<AABB> selectionBoxProvider;
    @Nullable private BlockObjectProvider<Collection<AABB>> collisionBoxesProvider;

    /**
     * The block sound group of this block type.
     */
    private BlockSoundGroup blockSoundGroup = BlockSoundGroups.STONE;

    // TODO: A better way to handle this?
    private final boolean isAir;

    LanternBlockType(CatalogKey key, Iterable<BlockTrait<?>> blockTraits,
            TranslationProvider translationProvider, MutableBehaviorPipeline<Behavior> behaviorPipeline,
            @Nullable BlockEntityProvider tileEntityProvider) {
        super(key);
        this.translationProvider = translationProvider;
        this.behaviorPipeline = behaviorPipeline;
        this.tileEntityProvider = tileEntityProvider;
        this.tickRandomly = !behaviorPipeline.pipeline(RandomTickBehavior.class).getBehaviors().isEmpty();
        this.blockStateBase = new LanternBlockStateMap(this, blockTraits);
        this.defaultBlockState = this.blockStateBase.getBaseState();
        this.isAir = key.getValue().contains("air");
    }

    void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    void setDefaultBlockState(BlockState blockState) {
        this.defaultBlockState = blockState;
    }

    public Optional<BlockEntityProvider> getBlockEntityProvider() {
        return Optional.ofNullable(this.tileEntityProvider);
    }

    /**
     * Gets the {@link MutableBehaviorPipeline} of this block type.
     *
     * @return The behavior pipeline
     */
    public MutableBehaviorPipeline<Behavior> getPipeline() {
        return this.behaviorPipeline;
    }

    /**
     * Gets the base of the block state.
     *
     * @return The block state base
     */
    public LanternBlockStateMap getBlockStateBase() {
        return this.blockStateBase;
    }

    @Override
    public Translation getTranslation() {
        return this.translationProvider.get(getDefaultState(), null, null);
    }

    /**
     * Gets the {@link Translation} of the specified block state, normally it should be
     * always the same, which means that the block state is ignored.
     *
     * @param blockState The block state
     * @return The translation
     */
    public Translation getTranslation(BlockState blockState) {
        return this.translationProvider.get(blockState, null, null);
    }

    /**
     * Gets the block state of this type from the target item stack.
     *
     * @param itemStack The item stack
     * @return The block state
     */
    public BlockState getStateFromItemStack(ItemStack itemStack) {
        return this.getDefaultState();
    }

    public PropertyProviderCollection getPropertyProviderCollection() {
        return this.propertyProviderCollection;
    }

    @Override
    public Optional<ItemType> getItem() {
        return Optional.ofNullable(this.itemType);
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
    public Collection<BlockState> getAllBlockStates() {
        return this.blockStateBase.getBlockStates();
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
        return getDefaultState().getTraits();
    }

    @Override
    public Optional<BlockTrait<?>> getTrait(String blockTrait) {
        return getDefaultState().getTrait(blockTrait);
    }

    @Override
    public BlockSoundGroup getSoundGroup() {
        return this.blockSoundGroup;
    }

    /**
     * Sets the block sound group of this block type.
     *
     * @param blockSoundGroup The block sound group
     */
    void setSoundGroup(BlockSoundGroup blockSoundGroup) {
        this.blockSoundGroup = blockSoundGroup;
    }

    void setPropertyProviderCollection(PropertyProviderCollection propertyProviderCollection) {
        this.propertyProviderCollection = propertyProviderCollection;
    }

    @Nullable
    public BlockObjectProvider<AABB> getSelectionBoxProvider() {
        return this.selectionBoxProvider;
    }

    void setSelectionBoxProvider(@Nullable BlockObjectProvider<AABB> selectionBoxProvider) {
        this.selectionBoxProvider = selectionBoxProvider;
    }

    @Nullable
    public BlockObjectProvider<Collection<AABB>> getCollisionBoxesProvider() {
        return this.collisionBoxesProvider;
    }

    void setCollisionBoxesProvider(@Nullable BlockObjectProvider<Collection<AABB>> boundingBoxProvider) {
        this.collisionBoxesProvider = boundingBoxProvider;
    }

    public boolean isAir() {
        return this.isAir;
    }
}
