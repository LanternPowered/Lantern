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

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.impl.MutableBehaviorPipelineImpl;
import org.lanternpowered.server.block.tile.LanternTileEntityType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.item.ItemTypeBuilder;
import org.lanternpowered.server.item.ItemTypeBuilderImpl;
import org.lanternpowered.server.item.behavior.simple.InteractWithBlockItemBehavior;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class BlockTypeBuilderImpl implements BlockTypeBuilder {

    @Nullable private ExtendedBlockStateProvider extendedBlockStateProvider;
    @Nullable private Function<BlockState, BlockState> defaultStateProvider;
    @Nullable private PropertyProviderCollection.Builder propertiesBuilder;
    @Nullable private MutableBehaviorPipeline<Behavior> behaviorPipeline;
    private final List<BlockTrait<?>> traits = new ArrayList<>();
    @Nullable private TranslationProvider translationProvider;
    @Nullable private TileEntityProvider tileEntityProvider;
    @Nullable private ItemTypeBuilder itemTypeBuilder;

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
        this.tileEntityProvider = (blockState, location, face) -> ((LanternTileEntityType) tileEntityType.get()).getTileEntityConstructor().get();
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
        return translation(Lantern.getRegistry().getTranslationManager().get(translation));
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
        this.itemTypeBuilder = new ItemTypeBuilderImpl();
        consumer.accept(this.itemTypeBuilder);
        return this;
    }

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
            translationProvider = TranslationProvider.of(Lantern.getRegistry().getTranslationManager().get(path));
        }
        PropertyProviderCollection properties;
        if (this.propertiesBuilder != null) {
            properties = this.propertiesBuilder.build();
        } else {
            properties = PropertyProviderCollections.DEFAULT;
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
        final LanternBlockType blockType = new LanternBlockType(pluginId, id, properties, this.traits,
                translationProvider, behaviorPipeline, this.tileEntityProvider, extendedBlockStateProvider);
        if (this.defaultStateProvider != null) {
            blockType.setDefaultBlockState(this.defaultStateProvider.apply(blockType.getDefaultState()));
        }
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
}
