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
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.behavior.simple.BlockSnapshotProviderPlaceBehavior;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.block.property.SolidSideProperty;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class TorchPlacementBehavior implements PlaceBlockBehavior {

    private static final Direction[] HORIZONTAL_DIRECTIONS = { Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST };

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        Direction face = context.tryGet(Parameters.INTERACTION_FACE);
        if (face == Direction.UP) {
            return BehaviorResult.PASS;
        }
        final BlockSnapshot snapshot = context.getCause().get(BlockSnapshotProviderPlaceBehavior.BLOCK_SNAPSHOT, BlockSnapshot.class)
                .orElseThrow(() -> new IllegalStateException("The BlockSnapshotProviderPlaceBehavior's BlockSnapshot isn't present."));
        final Location<World> location = context.tryGet(Parameters.BLOCK_LOCATION);
        final Location<World> clickLocation = context.tryGet(Parameters.INTERACTION_LOCATION);
        //noinspection ConstantConditions
        boolean flag = clickLocation.getExtent().getProperty(
                clickLocation.getBlockPosition(), face, SolidSideProperty.class).get().getValue();
        BlockSnapshotBuilder builder = BlockSnapshotBuilder.create().from(snapshot);
        if (!flag) {
            for (Direction direction : HORIZONTAL_DIRECTIONS) {
                //noinspection ConstantConditions
                flag = location.getExtent().getProperty(location.getBlockRelative(direction).getBlockPosition(),
                        direction.getOpposite(), SolidSideProperty.class).get().getValue();
                if (flag) {
                    face = direction;
                    break;
                }
            }
        }
        if (flag) {
            builder.add(Keys.DIRECTION, face.getOpposite());
        }
        context.addBlockChange(builder.location(location).build());
        return BehaviorResult.CONTINUE;
    }
}
