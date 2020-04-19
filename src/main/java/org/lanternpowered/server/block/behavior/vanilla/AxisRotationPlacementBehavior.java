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
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Axis;

public class AxisRotationPlacementBehavior implements PlaceBlockBehavior {

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        context.getContext(ContextKeys.INTERACTION_FACE).ifPresent(face -> {
            final Axis axis = Axis.getClosest(face.asOffset());
            context.transformBlockChanges((original, builder) -> builder.add(Keys.AXIS, axis));
        });
        return BehaviorResult.CONTINUE;
    }
}
