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

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import org.lanternpowered.server.block.state.LanternBlockStateMap;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.property.LanternPropertyRegistry;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.item.BlockItemType;
import org.lanternpowered.server.item.ItemInteractionResult;
import org.lanternpowered.server.item.ItemInteractionType;
import org.spongepowered.api.block.BlockSoundGroup;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class LanternBlockType extends PluginCatalogType.Base implements BlockType, AbstractPropertyHolder {

    /**
     * The default {@link ItemType} builder that can be used
     * to generate item types for the {@link BlockType}s.
     */
    public static final Function<BlockType, ItemType> DEFAULT_ITEM_TYPE_BUILDER =
            type -> new BlockItemType(((LanternBlockType) type).getPluginId(), type.getName(), type);

    private PropertyProviderCollection propertyProviderCollection = PropertyProviderCollections.DEFAULT;

    // The block state base which contains all the possible block states
    private final LanternBlockStateMap blockStateBase;
    private Translation translation;
    private final Optional<ItemType> itemType;
    private BlockState defaultBlockState;
    private boolean tickRandomly;

    public LanternBlockType(String pluginId, String identifier,
            @Nullable Function<BlockType, ItemType> itemTypeBuilder) {
        this(pluginId, identifier, itemTypeBuilder, Collections.emptyList());
    }

    public LanternBlockType(String pluginId, String identifier,
            @Nullable Function<BlockType, ItemType> itemTypeBuilder, BlockTrait<?>... blockTraits) {
        this(pluginId, identifier, itemTypeBuilder, Lists.newArrayList(blockTraits));
    }

    public LanternBlockType(String pluginId, String identifier, String translationKey,
            @Nullable Function<BlockType, ItemType> itemTypeBuilder, BlockTrait<?>... blockTraits) {
        this(pluginId, identifier, translationKey, itemTypeBuilder, Lists.newArrayList(blockTraits));
    }

    public LanternBlockType(String pluginId, String identifier,
            @Nullable Function<BlockType, ItemType> itemTypeBuilder, Iterable<BlockTrait<?>> blockTraits) {
        this(pluginId, identifier, identifier, itemTypeBuilder, blockTraits);
    }

    public LanternBlockType(String pluginId, String identifier, String translationKey,
            @Nullable Function<BlockType, ItemType> itemTypeBuilder, Iterable<BlockTrait<?>> blockTraits) {
        super(pluginId, identifier);

        this.translation = Lantern.getRegistry().getTranslationManager().get(
                "tile." + translationKey + ".name");
        this.blockStateBase = new LanternBlockStateMap(this, blockTraits);
        this.defaultBlockState = this.blockStateBase.getBaseState();
        // Create the block state base
        this.itemType = itemTypeBuilder == null ? Optional.empty() : Optional.of(itemTypeBuilder.apply(this));
    }

    protected void setDefaultState(BlockState blockState) {
        this.defaultBlockState = checkNotNull(blockState, "blockState");
    }

    protected void modifyDefaultState(Function<BlockState, BlockState> function) {
        this.defaultBlockState = checkNotNull(function.apply(this.defaultBlockState));
    }

    public PropertyProviderCollection getPropertyProviderCollection() {
        return this.propertyProviderCollection;
    }

    protected void modifyPropertyProviders(Consumer<PropertyProviderCollection.Builder> consumer) {
        final PropertyProviderCollection.Builder builder = this.propertyProviderCollection.toBuilder();
        consumer.accept(builder);
        this.setPropertyProviderCollection(builder.build());
    }

    protected void setPropertyProviderCollection(PropertyProviderCollection propertyProviderCollection) {
        this.propertyProviderCollection = checkNotNull(propertyProviderCollection, "propertyProviderCollection");
        LanternPropertyRegistry.getInstance().registerBlockPropertyStores(propertyProviderCollection);
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
        return this.translation;
    }

    /**
     * Gets the {@link Translation} of the specified block state, normally it should be
     * always the same, which means that the block state is ignored.
     *
     * @param blockState The block state
     * @return The translation
     */
    public Translation getTranslation(BlockState blockState) {
        return this.translation;
    }

    /**
     * Sets the {@link Translation} of this block type.
     *
     * @param translation The translation
     */
    protected void setTranslation(Translation translation) {
        this.translation = checkNotNull(translation, "translation");
    }

    /**
     * Gets whether the specified {@link BlockState} contains extra data.
     *
     * @param blockState The block state
     * @return Is extended state
     */
    public boolean isExtendedState(BlockState blockState) {
        return this.removeExtendedState(blockState) != blockState;
    }

    /**
     * Removes all the extended data from the state.
     *
     * @param blockState The block state
     * @return The block state without the extended data
     */
    public BlockState removeExtendedState(BlockState blockState) {
        return blockState;
    }

    /**
     * Gets the extended state for the specified block state, extra properties provided by surrounding blocks
     * may be applied in this method.
     *
     * @param blockState The block state
     * @param location The location
     * @return The actual state
     */
    public BlockState getExtendedState(BlockState blockState, Location<World> location) {
        return blockState;
    }

    /**
     * Gets the extended state for the specified location, extra properties provided by surrounding blocks
     * may be applied in this method.
     *
     * @param location The location
     * @return The actual state
     */
    public BlockState getExtendedState(Location<World> location) {
        return this.getExtendedState(location.getBlock(), location);
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

    /**
     * Performs a random tick at the specified location for a specific block state.
     *
     * @param location The location
     * @param blockState The block state
     */
    public void doRandomTickAt(Location<World> location, BlockState blockState) {
    }

    @Override
    public Optional<ItemType> getItem() {
        return this.itemType;
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
        return this.getDefaultState().getTraits();
    }

    @Override
    public Optional<BlockTrait<?>> getTrait(String blockTrait) {
        return this.getDefaultState().getTrait(blockTrait);
    }

    @Override
    public BlockSoundGroup getSoundGroup() {
        return null; // TODO
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

    public Optional<BlockState> placeBlockAt(@Nullable Player player, ItemStack itemStack,
            ItemInteractionType interactionType, Location<World> location, Direction blockFace) {
        return Optional.of(this.getStateFromItemStack(itemStack));
    }

    public ItemInteractionResult onInteractWithItemAt(@Nullable Player player, @Nullable ItemStack itemStack,
            ItemInteractionType interactionType, Location<World> clickedLocation, Direction blockFace) {
        return ItemInteractionResult.pass();
    }
}
