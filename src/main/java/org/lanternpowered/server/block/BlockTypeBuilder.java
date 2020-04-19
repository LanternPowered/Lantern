/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.block;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline;
import org.lanternpowered.server.block.provider.BlockObjectProvider;
import org.lanternpowered.server.block.provider.property.PropertyProviderCollection;
import org.lanternpowered.server.item.ItemTypeBuilder;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.AABB;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface BlockTypeBuilder {

    BlockTypeBuilder selectionBox(@Nullable AABB selectionBox);

    BlockTypeBuilder selectionBox(@Nullable Function<BlockState, AABB> selectionBoxProvider);

    BlockTypeBuilder selectionBox(@Nullable BlockObjectProvider<AABB> selectionBoxProvider);

    BlockTypeBuilder collisionBox(@Nullable AABB collisionBox);

    BlockTypeBuilder collisionBox(@Nullable Function<BlockState, AABB> collisionBoxProvider);

    BlockTypeBuilder collisionBox(@Nullable BlockObjectProvider<AABB> collisionBoxProvider);

    BlockTypeBuilder collisionBoxes(@Nullable Collection<AABB> collisionBoxes);

    BlockTypeBuilder collisionBoxes(@Nullable Function<BlockState, Collection<AABB>> collisionBoxesProvider);

    BlockTypeBuilder collisionBoxes(@Nullable BlockObjectProvider<Collection<AABB>> collisionBoxesProvider);

    BlockTypeBuilder defaultState(Function<BlockState, BlockState> function);

    BlockTypeBuilder properties(PropertyProviderCollection collection);

    BlockTypeBuilder properties(Consumer<PropertyProviderCollection.Builder> consumer);

    BlockTypeBuilder tileEntityType(Supplier<TileEntityType> tileEntityType);

    BlockTypeBuilder tileEntity(Supplier<TileEntity> supplier);

    BlockTypeBuilder tileEntity(BlockEntityProvider tileEntityProvider);

    BlockTypeBuilder traits(BlockTrait<?>... blockTraits);

    BlockTypeBuilder trait(BlockTrait<?> blockTrait);

    BlockTypeBuilder translation(String translation);

    BlockTypeBuilder translation(Translation translation);

    BlockTypeBuilder translation(TranslationProvider translationProvider);

    BlockTypeBuilder behaviors(BehaviorPipeline<Behavior> behaviorPipeline);

    BlockTypeBuilder behaviors(Consumer<MutableBehaviorPipeline<Behavior>> consumer);

    BlockTypeBuilder itemType();

    BlockTypeBuilder itemType(Consumer<ItemTypeBuilder> consumer);

    LanternBlockType build(String pluginId, String id);
}
