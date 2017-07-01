package org.lanternpowered.server.block.behavior.simple;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.behavior.types.BlockDropsProviderBehavior;
import org.lanternpowered.server.block.behavior.types.BreakBlockBehavior;

public class SimpleAddDropsBreakBehavior implements BreakBlockBehavior {

    @Override
    public BehaviorResult tryBreak(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        context.process(pipeline.pipeline(BlockDropsProviderBehavior.class), (ctx, behavior) -> {
            behavior.tryAddDrops(pipeline, context);
            return BehaviorResult.CONTINUE;
        });
        return BehaviorResult.CONTINUE;
    }
}
