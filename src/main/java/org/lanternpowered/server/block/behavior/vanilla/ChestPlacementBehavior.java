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
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ChestPlacementBehavior implements PlaceBlockBehavior {

    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final BlockSnapshot snapshot = context.getCause().get(BlockSnapshotProviderPlaceBehavior.BLOCK_SNAPSHOT, BlockSnapshot.class)
                .orElseThrow(() -> new IllegalStateException("The BlockSnapshotProviderPlaceBehavior's BlockSnapshot isn't present."));
        final Location<World> location = context.tryGet(Parameters.BLOCK_LOCATION);

        Location<World> otherChestLoc = null;
        // Check whether the chest already a double chest is,
        // and fail if this is the case
        for (Direction directionToCheck : HORIZONTAL_DIRECTIONS) {
            otherChestLoc = location.getRelative(directionToCheck);
            // We found a chest
            if (otherChestLoc.getBlock().getType() == snapshot.getState().getType()) {
                // Check if it isn't already double
                for (Direction directionToCheck1 : HORIZONTAL_DIRECTIONS) {
                    final Location<World> loc1 = otherChestLoc.getRelative(directionToCheck1);
                    if (loc1.getBlock().getType() == this) {
                        return BehaviorResult.FAIL;
                    }
                }
            } else {
                otherChestLoc = null;
            }
        }

        final LanternPlayer player = (LanternPlayer) context.get(Parameters.PLAYER).orElse(null);

        // Get the direction the chest should face
        final Direction facing;
        if (player != null) {
            facing = player.getHorizontalDirection(Direction.Division.CARDINAL).getOpposite();
        } else {
            facing = Direction.NORTH;
        }

        context.addBlockChange(BlockSnapshotBuilder.create().from(snapshot)
                .location(location).add(Keys.DIRECTION, facing).build());

        // Rotate the other chest in the same one as we placed
        if (otherChestLoc != null) {
            context.addBlockChange(BlockSnapshot.builder()
                    .from(otherChestLoc.createSnapshot())
                    .add(Keys.DIRECTION, facing)
                    .build());
        }

        return BehaviorResult.SUCCESS;
    }
}
