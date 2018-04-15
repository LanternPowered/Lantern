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

import com.flowpowered.math.vector.Vector3d;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ChestPlacementBehavior implements PlaceBlockBehavior {

    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[] { Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST };

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final BlockSnapshot snapshot = context.getContext(ContextKeys.BLOCK_SNAPSHOT)
                .orElseThrow(() -> new IllegalStateException("The BlockSnapshotProviderPlaceBehavior's BlockSnapshot isn't present."));
        final Location<World> location = context.requireContext(ContextKeys.BLOCK_LOCATION);
        final LanternPlayer player = (LanternPlayer) context.getContext(ContextKeys.PLAYER).orElse(null);

        // Get the direction the chest should face
        Direction facing;
        if (player != null) {
            facing = player.getHorizontalDirection(Direction.Division.CARDINAL).getOpposite();
        } else {
            facing = Direction.NORTH;
        }

        Location<World> otherChestLoc;
        // Check whether the chest already a double chest is,
        // and fail if this is the case
        for (Direction directionToCheck : HORIZONTAL_DIRECTIONS) {
            otherChestLoc = location.getRelative(directionToCheck);
            // We found a chest
            if (otherChestLoc.getBlock().getType() == snapshot.getState().getType()) {
                // Check if it isn't already double
                for (Direction directionToCheck1 : HORIZONTAL_DIRECTIONS) {
                    final Location<World> loc1 = otherChestLoc.getRelative(directionToCheck1);
                    if (loc1.getBlock().getType() == snapshot.getState().getType()) {
                        return BehaviorResult.FAIL;
                    }
                }
                final Direction otherFacing = otherChestLoc.get(Keys.DIRECTION).get();
                // The chests don't have the same facing direction, we need to fix it
                boolean flag = directionToCheck != facing && directionToCheck.getOpposite() != facing;
                if (!(facing == otherFacing && flag)) {
                    // The rotation of the chests is completely wrong, fix it
                    if (!flag) {
                        if (player != null) {
                            final Vector3d dir = player.getHorizontalDirectionVector();
                            if (directionToCheck == Direction.EAST || directionToCheck == Direction.WEST) {
                                facing = dir.getZ() >= 0 ? Direction.NORTH : Direction.SOUTH;
                            } else {
                                facing = dir.getX() >= 0 ? Direction.WEST : Direction.EAST;
                            }
                        } else {
                            facing = directionToCheck == Direction.EAST || directionToCheck == Direction.WEST ? Direction.SOUTH : Direction.EAST;
                        }
                    }
                    context.addBlockChange(BlockSnapshot.builder()
                            .from(otherChestLoc.createSnapshot())
                            .add(Keys.DIRECTION, facing)
                            .build());
                }
                break;
            }
        }

        context.addBlockChange(BlockSnapshotBuilder.create().from(snapshot)
                .location(location).add(Keys.DIRECTION, facing).build());
        return BehaviorResult.CONTINUE;
    }
}
