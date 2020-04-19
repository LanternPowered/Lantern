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
package org.lanternpowered.server.block.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.behavior.types.BreakBlockBehavior;
import org.lanternpowered.server.block.entity.vanilla.LanternChest;
import org.lanternpowered.server.block.state.BlockStateProperties;
import org.lanternpowered.server.data.type.LanternChestAttachment;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

public class ChestBreakBehavior implements BreakBlockBehavior {

    @Override
    public BehaviorResult tryBreak(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        Location location = context.requireContext(ContextKeys.BLOCK_LOCATION);
        final BlockState state = location.getBlock();
        final Direction connectionDir = LanternChest.getConnectedDirection(state);
        if (connectionDir != Direction.NONE) {
            location = location.getBlockRelative(connectionDir);
            final BlockState relState = location.getBlock();
            if (relState.getType() == state.getType() &&
                    relState.getTraitValue(BlockStateProperties.HORIZONTAL_FACING).get() ==
                    state.getTraitValue(BlockStateProperties.HORIZONTAL_FACING).get()) {
                final LanternChestAttachment relConnection = relState.getTraitValue(BlockStateProperties.CHEST_ATTACHMENT).get();
                final LanternChestAttachment connection = state.getTraitValue(BlockStateProperties.CHEST_ATTACHMENT).get();
                if ((relConnection == LanternChestAttachment.LEFT && connection == LanternChestAttachment.RIGHT) ||
                        (relConnection == LanternChestAttachment.RIGHT && connection == LanternChestAttachment.LEFT)) {
                    context.addBlockChange(BlockSnapshot.builder()
                            .from(location)
                            .add(Keys.CHEST_ATTACHMENT, LanternChestAttachment.SINGLE)
                            .build());
                }
            }
        }
        return BehaviorResult.CONTINUE;
    }
}
