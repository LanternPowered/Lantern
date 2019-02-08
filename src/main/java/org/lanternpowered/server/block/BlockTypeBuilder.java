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

import javax.annotation.Nullable;

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

    BlockTypeBuilder tileEntity(TileEntityProvider tileEntityProvider);

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
