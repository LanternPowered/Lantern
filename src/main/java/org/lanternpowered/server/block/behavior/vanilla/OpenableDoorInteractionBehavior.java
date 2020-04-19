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
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternDoorHalf;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

public class OpenableDoorInteractionBehavior extends OpenableInteractionBehavior {

    public OpenableDoorInteractionBehavior(OpenCloseAccess access) {
        super(access);
    }

    public OpenableDoorInteractionBehavior() {
        super();
    }

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        BehaviorResult result = super.tryInteract(pipeline, context);
        if (result != BehaviorResult.SUCCESS && result != BehaviorResult.CONTINUE) {
            return result;
        }

        final Location location = context.requireContext(ContextKeys.BLOCK_LOCATION);
        final BlockState baseState = location.getBlock();
        final LanternDoorHalf half = baseState.get(LanternKeys.DOOR_HALF).orElse(null);
        if (half == null) {
            return result;
        }

        final Direction dir = half == LanternDoorHalf.LOWER ? Direction.UP : Direction.DOWN;
        final LanternDoorHalf other = half == LanternDoorHalf.LOWER ? LanternDoorHalf.UPPER : LanternDoorHalf.LOWER;
        final Location loc = location.getBlockRelative(dir);

        BlockState otherState = loc.getBlock();
        if (otherState.get(LanternKeys.DOOR_HALF).orElse(null) == other &&
                otherState.with(LanternKeys.DOOR_HALF, half).orElse(null) == baseState) {
            result = super.tryInteract(pipeline, context);
        }

        return result;
    }
}
