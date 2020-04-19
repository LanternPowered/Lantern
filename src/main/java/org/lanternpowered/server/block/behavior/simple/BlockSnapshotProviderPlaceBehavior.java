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
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Key;
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
        final Optional<ItemStack> optItemStack = context.getContext(ContextKeys.USED_ITEM_STACK);
        if (optItemStack.isPresent()) {
            final ItemStack itemStack = optItemStack.get();
            final Optional<BlockType> optBlockType = itemStack.getType().getBlock();
            final BlockState blockState = optBlockType.map(BlockType::getDefaultState)
                    .orElseGet(() -> context.getContext(ContextKeys.USED_BLOCK_STATE).orElseThrow(IllegalStateException::new));
            builder.blockState(blockState);
            itemStack.getValues().forEach(value -> builder.add((Key) value.getKey(), value.get()));
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
