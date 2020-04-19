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
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Direction;

public class RotationPlacementBehavior implements PlaceBlockBehavior {

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final LanternPlayer player = (LanternPlayer) context.getContext(ContextKeys.PLAYER).orElse(null);

        // Get the direction the chest should face
        final Direction facing;
        if (player != null) {
            if (player.getPosition().getY() - context.requireContext(ContextKeys.BLOCK_LOCATION).getBlockPosition().getY() >= 0.5) {
                facing = player.getDirection(Direction.Division.CARDINAL).getOpposite();
            } else {
                facing = player.getHorizontalDirection(Direction.Division.CARDINAL).getOpposite();
            }
        } else {
            facing = Direction.NORTH;
        }

        context.transformBlockChanges((snapshot, builder) -> builder.add(Keys.DIRECTION, facing));
        return BehaviorResult.CONTINUE;
    }
}
