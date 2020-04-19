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
import org.lanternpowered.server.block.behavior.types.BlockDropsProviderBehavior;
import org.lanternpowered.server.block.behavior.types.BreakBlockBehavior;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.event.cause.EventContextKeys;

public class SimpleBreakBehavior implements BreakBlockBehavior {

    @Override
    public BehaviorResult tryBreak(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.create();
        // Apply the creator and notifier to the block
        context.populateBlockSnapshot(builder, BehaviorContext.PopulationFlags.CREATOR_AND_NOTIFIER);
        builder.location(context.requireContext(ContextKeys.BLOCK_LOCATION));
        // The block should be replaced with air
        builder.blockState(BlockTypes.AIR.getDefaultState());
        // Add the block change
        context.addBlockChange(builder.build());
        context.getContext(EventContextKeys.PLAYER).ifPresent(p -> p.get(Keys.EXHAUSTION)
                .ifPresent(value -> p.offer(Keys.EXHAUSTION, value + 0.005)));
        context.process(pipeline.pipeline(BlockDropsProviderBehavior.class), (ctx, behavior) -> {
            behavior.tryAddDrops(pipeline, context);
            return BehaviorResult.CONTINUE;
        });
        return BehaviorResult.CONTINUE;
    }
}
