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
import org.lanternpowered.server.block.behavior.types.BlockDropsProviderBehavior;
import org.lanternpowered.server.block.behavior.types.BreakBlockBehavior;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
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
