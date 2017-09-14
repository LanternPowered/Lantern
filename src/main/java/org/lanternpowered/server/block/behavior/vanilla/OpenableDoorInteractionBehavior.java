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
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternDoorHalf;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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

        final Location<World> location = context.requireContext(ContextKeys.BLOCK_LOCATION);
        final BlockState baseState = location.getBlock();
        final LanternDoorHalf half = baseState.get(LanternKeys.DOOR_HALF).orElse(null);
        if (half == null) {
            return result;
        }

        final Direction dir = half == LanternDoorHalf.LOWER ? Direction.UP : Direction.DOWN;
        final LanternDoorHalf other = half == LanternDoorHalf.LOWER ? LanternDoorHalf.UPPER : LanternDoorHalf.LOWER;
        final Location<World> loc = location.getBlockRelative(dir);

        BlockState otherState = loc.getBlock();
        if (otherState.get(LanternKeys.DOOR_HALF).orElse(null) == other &&
                otherState.with(LanternKeys.DOOR_HALF, half).orElse(null) == baseState) {
            result = super.tryInteract(pipeline, context);
        }

        return result;
    }
}
