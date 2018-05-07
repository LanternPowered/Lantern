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
package org.lanternpowered.server.block.behavior.simple;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

/**
 * Extracts the {@link BlockSnapshot} from the {@link ItemStack} with the key
 * {@link ContextKeys#USED_ITEM_STACK} and puts it into the context.
 * If the used item stack isn't present, then will snapshot be based of the default
 * block state.
 * Note: The snapshot will <strong>not</strong> contain a <strong>location</strong>.
 */
@SuppressWarnings("unchecked")
public class BlockSnapshotProviderPlaceBehavior implements PlaceBlockBehavior {

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final BlockSnapshot.Builder builder = BlockSnapshotBuilder.createPositionless();
        final Optional<ItemStack> optItem = context.getContext(ContextKeys.USED_ITEM_STACK);
        if (optItem.isPresent()) {
            builder.blockState(optItem.get().getType().getBlock().get().getDefaultState());
            optItem.get().getValues().forEach(value -> builder.add((Key) value.getKey(), value.get()));
        } else {
            final Optional<BlockState> optState = context.getContext(ContextKeys.USED_BLOCK_STATE);
            if (optState.isPresent()) {
                builder.blockState(optState.get());
            } else {
                throw new IllegalStateException();
            }
        }
        context.populateBlockSnapshot(builder, BehaviorContext.PopulationFlags.CREATOR | BehaviorContext.PopulationFlags.NOTIFIER);
        context.addContext(ContextKeys.BLOCK_SNAPSHOT, builder.build());
        return BehaviorResult.CONTINUE;
    }
}
