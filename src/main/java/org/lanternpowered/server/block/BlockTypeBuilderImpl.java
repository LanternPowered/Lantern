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
import static org.lanternpowered.server.block.provider.property.PropertyProviders.blockSoundGroup;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.fullBlockSelectionBox;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.solidCube;
import static org.lanternpowered.server.block.provider.property.PropertyProviders.solidSide;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.impl.MutableBehaviorPipelineImpl;
import org.lanternpowered.server.block.aabb.BoundingBoxes;
import org.lanternpowered.server.block.property.BlockSoundGroupProperty;
import org.lanternpowered.server.block.property.SolidSideProperty;
import org.lanternpowered.server.block.provider.CachedSimpleObjectProvider;
import org.lanternpowered.server.block.provider.ConstantObjectProvider;
import org.lanternpowered.server.block.provider.ObjectProvider;
import org.lanternpowered.server.block.provider.SimpleObjectProvider;
import org.lanternpowered.server.block.provider.property.CachedPropertyObjectProvider;
import org.lanternpowered.server.block.provider.property.ConstantPropertyProvider;
import org.lanternpowered.server.block.provider.property.PropertyConstants;
import org.lanternpowered.server.block.provider.property.PropertyProvider;
import org.lanternpowered.server.block.provider.property.PropertyProviderCollection;
import org.lanternpowered.server.block.provider.property.PropertyProviderCollections;
import org.lanternpowered.server.block.provider.property.SimplePropertyProvider;
import org.lanternpowered.server.block.state.LanternBlockState;
import org.lanternpowered.server.block.tile.LanternTileEntityType;
import org.lanternpowered.server.item.ItemTypeBuilder;
import org.lanternpowered.server.item.ItemTypeBuilderImpl;
import org.lanternpowered.server.item.behavior.simple.InteractWithBlockItemBehavior;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockSoundGroup;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.block.FullBlockSelectionBoxProperty;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings({"ConstantConditions", "unchecked"})
public class BlockTypeBuilderImpl implements BlockTypeBuilder {

    private static class SingleCollisionBoxProvider implements ObjectProvider<Collection<AABB>> {

        private final ObjectProvider<AABB> collisionBoxProvider;

        SingleCollisionBoxProvider(
                ObjectProvider<AABB> collisionBoxProvider) {
            this.collisionBoxProvider = collisionBoxProvider;
        }

        @Override
        public Collection<AABB> get(BlockState blockState,
                @Nullable Location<World> location, @Nullable Direction face) {
            return Collections.singletonList(this.collisionBoxProvider.get(blockState, location, face));
        }
    }

    private static final ObjectProvider<AABB> defaultCollisionBoxProvider =
            new ConstantObjectProvider<>(BoundingBoxes.DEFAULT);
    private static final ObjectProvider<Collection<AABB>> defaultCollisionBoxesProvider =
            new SingleCollisionBoxProvider(defaultCollisionBoxProvider);

    @Nullable private ExtendedBlockStateProvider extendedBlockStateProvider;
    @Nullable private Function<BlockState, BlockState> defaultStateProvider;
    @Nullable private PropertyProviderCollection.Builder propertiesBuilder;
    @Nullable private MutableBehaviorPipeline<Behavior> behaviorPipeline;
    private final List<BlockTrait<?>> traits = new ArrayList<>();
    @Nullable private TranslationProvider translationProvider;
    @Nullable private TileEntityProvider tileEntityProvider;
    @Nullable private ItemTypeBuilder itemTypeBuilder;

    @Nullable private ObjectProvider<AABB> selectionBoxProvider = null;
    @Nullable private ObjectProvider<Collection<AABB>> collisionBoxesProvider = defaultCollisionBoxesProvider;

    @Override
    public BlockTypeBuilder selectionBox(@Nullable AABB boundingBox) {
        return selectionBox(boundingBox == null ? null : new ConstantObjectProvider<>(boundingBox));
    }

    @Override
    public BlockTypeBuilder selectionBox(@Nullable Function<BlockState, AABB> boundingBoxProvider) {
        return selectionBox(new SimpleObjectProvider<>(boundingBoxProvider));
    }

    @Override
    public BlockTypeBuilder selectionBox(@Nullable ObjectProvider<AABB> boundingBoxProvider) {
        this.selectionBoxProvider = boundingBoxProvider;
        return this;
    }

    @Override
    public BlockTypeBuilder collisionBox(@Nullable AABB collisionBox) {
        return collisionBox(collisionBox == null ? null : new ConstantObjectProvider<>(collisionBox));
    }

    @Override
    public BlockTypeBuilder collisionBox(@Nullable Function<BlockState, AABB> collisionBoxProvider) {
        return collisionBox(new SimpleObjectProvider<>(collisionBoxProvider));
    }

    @Override
    public BlockTypeBuilder collisionBox(@Nullable ObjectProvider<AABB> collisionBoxProvider) {
        return collisionBoxes(collisionBoxProvider == null ? null : new SingleCollisionBoxProvider(collisionBoxProvider));
    }

    @Override
    public BlockTypeBuilder collisionBoxes(@Nullable Collection<AABB> collisionBoxes) {
        return collisionBoxes(collisionBoxes == null ? null : new ConstantObjectProvider<>(collisionBoxes));
    }

    @Override
    public BlockTypeBuilder collisionBoxes(@Nullable Function<BlockState, Collection<AABB>> collisionBoxesProvider) {
        return collisionBoxes(collisionBoxesProvider == null ? null : new SimpleObjectProvider<>(collisionBoxesProvider));
    }

    @Override
    public BlockTypeBuilder collisionBoxes(@Nullable ObjectProvider<Collection<AABB>> collisionBoxesProvider) {
        this.collisionBoxesProvider = collisionBoxesProvider;
        return this;
    }

    @Override
    public BlockTypeBuilder defaultState(Function<BlockState, BlockState> function) {
        checkNotNull(function, "function");
        this.defaultStateProvider = function;
        return this;
    }

    @Override
    public BlockTypeBuilder extendedStateProvider(ExtendedBlockStateProvider provider) {
        checkNotNull(provider, "provider");
        this.extendedBlockStateProvider = provider;
        return this;
    }

    @Override
    public BlockTypeBuilder properties(PropertyProviderCollection collection) {
        checkNotNull(collection, "collection");
        this.propertiesBuilder = collection.toBuilder();
        return this;
    }

    @Override
    public BlockTypeBuilder properties(Consumer<PropertyProviderCollection.Builder> consumer) {
        checkNotNull(consumer, "consumer");
        if (this.propertiesBuilder == null) {
            this.propertiesBuilder = PropertyProviderCollections.DEFAULT.toBuilder();
        }
        consumer.accept(this.propertiesBuilder);
        return this;
    }

    @Override
    public BlockTypeBuilderImpl tileEntityType(Supplier<TileEntityType> tileEntityType) {
        checkNotNull(tileEntityType, "tileEntityType");
        this.tileEntityProvider = (blockState, location, face) -> ((LanternTileEntityType) tileEntityType.get()).construct();
        return this;
    }

    @Override
    public BlockTypeBuilderImpl tileEntity(Supplier<TileEntity> supplier) {
        checkNotNull(supplier, "supplier");
        this.tileEntityProvider = TileEntityProvider.of(supplier);
        return this;
    }

    @Override
    public BlockTypeBuilderImpl tileEntity(TileEntityProvider tileEntityProvider) {
        checkNotNull(tileEntityProvider, "tileEntityProvider");
        this.tileEntityProvider = tileEntityProvider;
        return this;
    }

    @Override
    public BlockTypeBuilderImpl traits(BlockTrait<?>... blockTraits) {
        checkNotNull(blockTraits, "blockTraits");
        for (BlockTrait<?> blockTrait : blockTraits) {
            trait(blockTrait);
        }
        return this;
    }

    @Override
    public BlockTypeBuilderImpl trait(BlockTrait<?> blockTrait) {
        checkNotNull(blockTrait, "blockTrait");
        this.traits.add(blockTrait);
        return this;
    }

    @Override
    public BlockTypeBuilderImpl translation(String translation) {
        return translation(tr(translation));
    }

    @Override
    public BlockTypeBuilderImpl translation(Translation translation) {
        this.translationProvider = TranslationProvider.of(checkNotNull(translation, "translation"));
        return this;
    }

    @Override
    public BlockTypeBuilderImpl translation(TranslationProvider translationProvider) {
        this.translationProvider = checkNotNull(translationProvider, "translationProvider");
        return this;
    }

    @Override
    public BlockTypeBuilderImpl behaviors(BehaviorPipeline<Behavior> behaviorPipeline) {
        checkNotNull(behaviorPipeline, "behaviorPipeline");
        this.behaviorPipeline = new MutableBehaviorPipelineImpl<>(Behavior.class, behaviorPipeline.getBehaviors());
        return this;
    }

    @Override
    public BlockTypeBuilderImpl behaviors(Consumer<MutableBehaviorPipeline<Behavior>> consumer) {
        checkNotNull(consumer, "consumer");
        if (this.behaviorPipeline == null) {
            this.behaviorPipeline = new MutableBehaviorPipelineImpl<>(Behavior.class, new ArrayList<>());
        }
        consumer.accept(this.behaviorPipeline);
        return this;
    }

    @Override
    public BlockTypeBuilderImpl itemType() {
        this.itemTypeBuilder = new ItemTypeBuilderImpl();
        return this;
    }

    @Override
    public BlockTypeBuilderImpl itemType(Consumer<ItemTypeBuilder> consumer) {
        checkNotNull(consumer, "consumer");
        if (this.itemTypeBuilder == null) {
            this.itemTypeBuilder = new ItemTypeBuilderImpl();
        }
        consumer.accept(this.itemTypeBuilder);
        return this;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public LanternBlockType build(String pluginId, String id) {
        MutableBehaviorPipeline<Behavior> behaviorPipeline = this.behaviorPipeline;
        if (behaviorPipeline == null) {
            behaviorPipeline = new MutableBehaviorPipelineImpl<>(Behavior.class, new ArrayList<>());
        } else {
            behaviorPipeline = new MutableBehaviorPipelineImpl<>(Behavior.class, new ArrayList<>(behaviorPipeline.getBehaviors()));
        }
        TranslationProvider translationProvider = this.translationProvider;
        if (translationProvider == null) {
            String path = "tile." + id + ".name";
            if (!pluginId.equals("minecraft")) {
                path = pluginId + '.' + path;
            }
            translationProvider = TranslationProvider.of(tr(path));
        }
        PropertyProviderCollection.Builder properties;
        if (this.propertiesBuilder != null) {
            properties = this.propertiesBuilder;
        } else {
            properties = PropertyProviderCollections.DEFAULT.toBuilder();
        }
        ExtendedBlockStateProvider extendedBlockStateProvider = this.extendedBlockStateProvider;
        if (extendedBlockStateProvider == null) {
            extendedBlockStateProvider = new ExtendedBlockStateProvider() {
                @Override
                public BlockState get(BlockState blockState, @Nullable Location<World> location, @Nullable Direction face) {
                    return blockState;
                }

                @Override
                public BlockState remove(BlockState blockState) {
                    return blockState;
                }
            };
        }
        final LanternBlockType blockType = new LanternBlockType(pluginId, id, this.traits,
                translationProvider, behaviorPipeline, this.tileEntityProvider, extendedBlockStateProvider);
        // Override the default solid cube property provider if necessary
        final PropertyProvider<SolidCubeProperty> solidCubeProvider = properties.build().get(SolidCubeProperty.class).orElse(null);
        final PropertyProvider<SolidSideProperty> solidSideProvider = properties.build().get(SolidSideProperty.class).orElse(null);
        final PropertyProvider<PassableProperty> passableProvider = properties.build().get(PassableProperty.class).orElse(null);
        ObjectProvider<Collection<AABB>> collisionBoxesProvider0 = this.collisionBoxesProvider;
        if (collisionBoxesProvider0 == defaultCollisionBoxesProvider) {
            if (passableProvider instanceof ConstantObjectProvider &&
                    passableProvider.get(null, null, null).getValue()) {
                collisionBoxesProvider0 = null;
            } else if (passableProvider instanceof SimpleObjectProvider) {
                final Function<BlockState, PassableProperty> passableFunction = ((SimpleObjectProvider) passableProvider).getFunction();
                collisionBoxesProvider0 = new SimpleObjectProvider<>(blockState -> passableFunction.apply(blockState).getValue() ?
                        Collections.singletonList(BoundingBoxes.DEFAULT) : Collections.emptyList());
            }
        }
        final ObjectProvider<Collection<AABB>> collisionBoxesProvider;
        if (collisionBoxesProvider0 instanceof SimpleObjectProvider) {
            collisionBoxesProvider = new CachedSimpleObjectProvider(blockType,
                    ((SimpleObjectProvider) collisionBoxesProvider0).getFunction());
        } else {
            collisionBoxesProvider = collisionBoxesProvider0;
        }
        if (solidCubeProvider == null) {
            if (collisionBoxesProvider instanceof ConstantObjectProvider) {
                final Collection<AABB> collisionBoxes = collisionBoxesProvider.get(null, null, null);
                final boolean isSolid = isSolid(collisionBoxes);
                if (isSolid) {
                    properties.add(solidCube(true));
                    properties.add(solidSide(true));
                } else {
                    properties.add(solidCube(false));
                    final BitSet solidSides = compileSidePropertyBitSet(collisionBoxes);
                    // Check if all the direction bits are set
                    final byte[] bytes = solidSides.toByteArray();
                    if (bytes.length == 0 || bytes[0] != (1 << DIRECTION_INDEXES) - 1) {
                        properties.add(solidSide((blockState, location, face) -> {
                            final int index = getDirectionIndex(face);
                            return index != -1 && solidSides.get(index);
                        }));
                    } else {
                        properties.add(solidSide(false));
                    }
                }
            } else if (collisionBoxesProvider instanceof CachedSimpleObjectProvider) {
                final List<Collection<AABB>> values = ((CachedSimpleObjectProvider<Collection<AABB>>) collisionBoxesProvider).getValues();
                final BitSet bitSet = new BitSet();
                int count = 0;
                for (int i = 0; i < values.size(); i++) {
                    if (isSolid(values.get(i))) {
                        bitSet.set(i);
                        count++;
                    }
                }
                final boolean flag1 = count == values.size();
                final boolean flag2 = count == 0;
                // Use the best possible solid cube property
                if (flag1) {
                    properties.add(solidCube(true));
                    properties.add(solidSide(false));
                } else if (flag2) {
                    properties.add(solidCube(false));
                } else {
                    properties.add(solidCube(((blockState, location, face) -> bitSet.get(((LanternBlockState) blockState).getInternalId()))));
                }
                if (!flag1) {
                    final BitSet[] solidSides = new BitSet[values.size()];
                    int solidCount = 0;
                    for (int i = 0; i < values.size(); i++) {
                        solidSides[i] = compileSidePropertyBitSet(values.get(i));
                        // Check if all the direction bits are set
                        final byte[] bytes = solidSides[i].toByteArray();
                        if (bytes.length != 0 && bytes[0] == (1 << DIRECTION_INDEXES) - 1) {
                            solidCount++;
                        }
                    }
                    if (solidCount == 0) {
                        properties.add(solidSide(false));
                    } else {
                        properties.add(solidSide((blockState, location, face) -> {
                            final int index = getDirectionIndex(face);
                            if (index == -1) {
                                return false;
                            }
                            final int state = ((LanternBlockState) blockState).getInternalId();
                            return solidSides[state].get(index);
                        }));
                    }
                }
            } else if (collisionBoxesProvider == null) {
                properties.add(solidCube(false));
                properties.add(solidSide(false));
            } else {
                properties.add(solidCube((blockState, location, face) ->
                        isSolid(collisionBoxesProvider.get(blockState, location, face))));
                properties.add(solidSide((blockState, location, face) ->
                        isSideSolid(collisionBoxesProvider.get(blockState, location, face), face)));
            }
        } else if (solidSideProvider == null) {
            properties.add(solidSide((blockState, location, face) ->
                    solidCubeProvider.get(blockState, location, face).getValue()));
        }
        ObjectProvider<AABB> selectionBoxProvider = this.selectionBoxProvider;
        if (selectionBoxProvider instanceof SimpleObjectProvider) {
            selectionBoxProvider = new CachedSimpleObjectProvider(blockType, ((SimpleObjectProvider) selectionBoxProvider).getFunction());
        } else if (selectionBoxProvider == null &&
                collisionBoxesProvider != null) {
            // A collision boxes provider is present, but no selection box,
            // so generate the selection box based on the collision boxes
            if (this.collisionBoxesProvider == defaultCollisionBoxesProvider) {
                selectionBoxProvider = new ConstantObjectProvider<>(BoundingBoxes.DEFAULT);
            } else if (collisionBoxesProvider instanceof ConstantObjectProvider) {
                final Collection<AABB> collisionBoxes = collisionBoxesProvider.get(null, null, null);
                selectionBoxProvider = new ConstantObjectProvider<>(unionAABB(collisionBoxes));
            } else if (collisionBoxesProvider instanceof CachedSimpleObjectProvider) {
                final Function<BlockState, Collection<AABB>> provider = ((CachedSimpleObjectProvider) collisionBoxesProvider).getFunction();
                selectionBoxProvider = new CachedSimpleObjectProvider<>(blockType, provider.andThen(BlockTypeBuilderImpl::unionAABB));
            } else {
                selectionBoxProvider = (blockState, location, face) ->
                        unionAABB(collisionBoxesProvider.get(blockState, location, face));
            }
        }
        blockType.setSelectionBoxProvider(selectionBoxProvider);
        blockType.setCollisionBoxesProvider(collisionBoxesProvider);
        blockType.setPropertyProviderCollection(properties.build());
        if (this.defaultStateProvider != null) {
            blockType.setDefaultBlockState(this.defaultStateProvider.apply(blockType.getDefaultState()));
        }
        final Optional<PropertyProvider<BlockSoundGroupProperty>> optProvider = properties.build().get(BlockSoundGroupProperty.class);
        // Apply the default block sound group property if missing
        if (optProvider.isPresent()) {
            final BlockSoundGroup blockSoundGroup = optProvider.get().get(blockType.getDefaultState(), null, null).getValue();
            if (blockSoundGroup != null) {
                blockType.setSoundGroup(blockSoundGroup);
            }
        } else if (collisionBoxesProvider != null) {
            if (passableProvider instanceof ConstantObjectProvider) {
                if (passableProvider.get(blockType.getDefaultState(), null, null).getValue()) {
                    properties.add(blockSoundGroup(null));
                } else {
                    properties.add(blockSoundGroup(blockType.getSoundGroup())); // Use the default sound group
                }
            } else {
                final BlockSoundGroupProperty defaultSoundGroup = new BlockSoundGroupProperty(blockType.getSoundGroup());
                final BlockSoundGroupProperty noSoundGroup = new BlockSoundGroupProperty(null);
                properties.add(BlockSoundGroupProperty.class, (blockState, location, face) ->
                        passableProvider.get(blockState, location, face).getValue() ? noSoundGroup : defaultSoundGroup);
            }
        }
        PropertyProvider<FullBlockSelectionBoxProperty> fullBlockSelectionBoxProvider =
                properties.build().get(FullBlockSelectionBoxProperty.class).orElse(null);
        if (fullBlockSelectionBoxProvider == null) {
            if (selectionBoxProvider instanceof ConstantPropertyProvider) {
                properties.add(fullBlockSelectionBox(isFullBlockAABB(selectionBoxProvider.get(null, null, null))));
            } else if (selectionBoxProvider instanceof CachedSimpleObjectProvider) {
                final Function<BlockState, AABB> aabbFunction = ((CachedSimpleObjectProvider) selectionBoxProvider).getFunction();
                properties.add(FullBlockSelectionBoxProperty.class, new CachedPropertyObjectProvider<>(blockType, state ->
                        isFullBlockAABB(aabbFunction.apply(state)) ?
                                PropertyConstants.FULL_BLOCK_SELECTION_BOX_PROPERTY_TRUE :
                                PropertyConstants.FULL_BLOCK_SELECTION_BOX_PROPERTY_FALSE));
            } else if (selectionBoxProvider == null) {
                properties.add(fullBlockSelectionBox(false));
            } else {
                final ObjectProvider<AABB> selectionBoxProvider1 = selectionBoxProvider;
                properties.add(FullBlockSelectionBoxProperty.class, (blockState, location, face) ->
                        isFullBlockAABB(selectionBoxProvider1.get(blockState, location, face)) ?
                                PropertyConstants.FULL_BLOCK_SELECTION_BOX_PROPERTY_TRUE :
                                PropertyConstants.FULL_BLOCK_SELECTION_BOX_PROPERTY_FALSE);
            }
        }
        blockType.setPropertyProviderCollection(properties.build());
        final PropertyProviderCollection propertiesCollection = properties.build();
        final PropertyProviderCollection.Builder newProperties = PropertyProviderCollection.builder();
        for (Class<? extends Property<?,?>> key : propertiesCollection.keys()) {
            propertiesCollection.get(key).ifPresent(provider -> {
                if (provider instanceof SimplePropertyProvider) {
                    provider = new CachedPropertyObjectProvider<>(
                            blockType, ((SimplePropertyProvider) provider).getFunction());
                }
                newProperties.add((Class) key, provider);
            });
        }
        blockType.setPropertyProviderCollection(newProperties.build());
        if (this.itemTypeBuilder != null) {
            final ItemType itemType = this.itemTypeBuilder.blockType(blockType)
                    .behaviors(pipeline -> {
                        // Only add the default behavior if there isn't any interaction behavior present
                        if (pipeline.pipeline(InteractWithItemBehavior.class).getBehaviors().isEmpty()) {
                            pipeline.add(new InteractWithBlockItemBehavior());
                        }
                    })
                    .build(blockType.getPluginId(), blockType.getName());
            blockType.setItemType(itemType);
        }
        return blockType;
    }

    public static boolean isFullBlockAABB(AABB aabb) {
        final Vector3d min = aabb.getMin();
        final Vector3d max = aabb.getMax();
        return min.getX() <= 0 && min.getY() <= 0 && min.getZ() <= 0 &&
                max.getX() >= 1 && max.getY() >= 1 && max.getZ() >= 1;
    }

    private static AABB unionAABB(Collection<AABB> aabbs) {
        Vector3d min = null;
        Vector3d max = null;
        for (AABB aabb : aabbs) {
            if (min == null) {
                min = aabb.getMin();
                max = aabb.getMax();
            } else {
                min = aabb.getMin().min(min);
                max = aabb.getMax().max(max);
            }
        }
        return new AABB(min, max);
    }

    private static boolean isSolid(Collection<AABB> collisionBoxes) {
        if (collisionBoxes.size() != 1) {
            return false;
        }
        final AABB collisionBox = collisionBoxes.iterator().next();
        return collisionBox.getMin().equals(BoundingBoxes.DEFAULT.getMin()) &&
                collisionBox.getMax().equals(BoundingBoxes.DEFAULT.getMax());
    }

    private static BitSet compileSidePropertyBitSet(Collection<AABB> boundingBoxes) {
        final BitSet bitSet = new BitSet(DIRECTION_INDEXES);
        if (isSideSolid(boundingBoxes, Direction.DOWN)) {
            bitSet.set(INDEX_DOWN);
        }
        if (isSideSolid(boundingBoxes, Direction.UP)) {
            bitSet.set(INDEX_UP);
        }
        if (isSideSolid(boundingBoxes, Direction.WEST)) {
            bitSet.set(INDEX_WEST);
        }
        if (isSideSolid(boundingBoxes, Direction.EAST)) {
            bitSet.set(INDEX_EAST);
        }
        if (isSideSolid(boundingBoxes, Direction.NORTH)) {
            bitSet.set(INDEX_NORTH);
        }
        if (isSideSolid(boundingBoxes, Direction.SOUTH)) {
            bitSet.set(INDEX_SOUTH);
        }
        return bitSet;
    }

    private static boolean isSideSolid(Collection<AABB> collisionBoxes, @Nullable Direction face) {
        // The area of the face that is covered,
        // overlapping should never happen
        double area = 0;

        for (AABB collisionBox : collisionBoxes) {
            // Limit the AABB within a block position
            final Vector3d min = collisionBox.getMin().max(Vector3d.ZERO).min(Vector3d.ONE);
            final Vector3d max = collisionBox.getMax().min(Vector3d.ONE).max(Vector3d.ZERO);

            if (face == Direction.DOWN) {
                if (min.getY() != 0.0) {
                    continue;
                }
                area += (max.getX() - min.getX()) * (max.getZ() - min.getZ());
            } else if (face == Direction.UP) {
                if (max.getY() != 1.0) {
                    continue;
                }
                area += (max.getX() - min.getX()) * (max.getZ() - min.getZ());
            } else if (face == Direction.NORTH) {
                if (min.getZ() != 0.0) {
                    continue;
                }
                area += (max.getX() - min.getX()) * (max.getY() - min.getY());
            } else if (face == Direction.SOUTH) {
                if (max.getZ() != 1.0) {
                    continue;
                }
                area += (max.getX() - min.getX()) * (max.getY() - min.getY());
            } else if (face == Direction.WEST) {
                if (min.getX() != 0.0) {
                    continue;
                }
                area += (max.getZ() - min.getZ()) * (max.getY() - min.getY());
            } else if (face == Direction.EAST) {
                if (max.getX() != 1.0) {
                    continue;
                }
                area += (max.getZ() - min.getZ()) * (max.getY() - min.getY());
            }
        }

        return area >= 1.0;
    }

    private static final int DIRECTION_INDEXES = 6;

    private static final int INDEX_NORTH = 0;
    private static final int INDEX_SOUTH = 1;
    private static final int INDEX_WEST = 2;
    private static final int INDEX_EAST = 3;
    private static final int INDEX_UP = 4;
    private static final int INDEX_DOWN = 5;

    private static int getDirectionIndex(@Nullable Direction direction) {
        if (direction == null) {
            return -1;
        }
        switch (direction) {
            case NORTH:
                return 0;
            case SOUTH:
                return 1;
            case WEST:
                return 2;
            case EAST:
                return 3;
            case UP:
                return 4;
            case DOWN:
                return 5;
            default:
                return -1;
        }
    }
}
