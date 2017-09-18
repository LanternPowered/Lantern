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
package org.lanternpowered.server.block.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.QuartzType;
import org.spongepowered.api.data.type.QuartzTypes;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Direction;

public class QuartzLinesRotationPlacementBehavior implements PlaceBlockBehavior {

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Direction face = context.requireContext(ContextKeys.INTERACTION_FACE);
        final Axis axis = Axis.getClosest(face.asOffset());
        final QuartzType newQuartzType = axis == Axis.X ? QuartzTypes.LINES_X : axis == Axis.Y ? QuartzTypes.LINES_Y : QuartzTypes.LINES_Z;
        context.transformBlockChanges((original, builder) -> {
            final BlockState blockState = original.getState();
            QuartzType quartzType = blockState.get(Keys.QUARTZ_TYPE).orElse(null);
            if (quartzType == QuartzTypes.LINES_X || quartzType == QuartzTypes.LINES_Y || quartzType == QuartzTypes.LINES_Z) {
                builder.add(Keys.QUARTZ_TYPE, newQuartzType);
            }
        });
        return BehaviorResult.CONTINUE;
    }
}
