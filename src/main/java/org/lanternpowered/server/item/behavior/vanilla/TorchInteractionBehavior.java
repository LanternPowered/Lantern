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
package org.lanternpowered.server.item.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockProperties;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.state.BlockStateProperties;
import org.lanternpowered.server.entity.player.LanternPlayer;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.math.vector.Vector3d;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("ConstantConditions")
public class TorchInteractionBehavior implements InteractWithItemBehavior {

    private static Direction[] getHorizontalDirections(Vector3d vector) {
        Direction start = Direction.getClosestHorizontal(vector, Direction.Division.CARDINAL).getOpposite();
        Direction second;
        if (start == Direction.WEST || start == Direction.EAST) {
            final double d = vector.getZ();
            if (d > 0) {
                second = Direction.NORTH;
            } else {
                second = Direction.SOUTH;
            }
        } else {
            final double d = vector.getX();
            if (d > 0) {
                second = Direction.WEST;
            } else {
                second = Direction.EAST;
            }
        }
        return new Direction[] { start, second, start.getOpposite(), second.getOpposite() };
    }

    @Nullable
    private BlockSnapshotBuilder tryPlaceAt(LanternPlayer player, Location location) {
        Direction facing;
        if (player != null) {
            facing = player.getDirection(Direction.Division.CARDINAL).getOpposite();
            // The up direction cannot be used
            if (facing == Direction.DOWN) {
                facing = player.getHorizontalDirection(Direction.Division.CARDINAL).getOpposite();
            }
        } else {
            facing = Direction.UP;
        }
        Direction direction = null;
        if (facing == Direction.UP && location.getWorld().getProperty(
                location.relativeToBlock(Direction.DOWN).getBlockPosition(), Direction.UP, BlockProperties.IS_SOLID_SIDE).orElse(false)) {
            direction = Direction.UP;
        }
        if (direction == null) {
            for (Direction dir : getHorizontalDirections(player.getHorizontalDirectionVector())) {
                if (location.getWorld().getProperty(location.relativeToBlock(dir.getOpposite()).getBlockPosition(),
                        dir, BlockProperties.IS_SOLID_SIDE).orElse(false)) {
                    direction = dir;
                    break;
                }
            }
        }
        if (direction == null) {
            return null;
        }
        return createBuilder(direction);
    }

    private BlockSnapshotBuilder createBuilder(Direction direction) {
        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.create();
        if (direction == Direction.UP) {
            return builder.blockState(BlockTypes.TORCH.getDefaultState());
        } else {
            return builder.blockState(BlockTypes.WALL_TORCH.getDefaultState()
                    .withStateProperty(BlockStateProperties.HORIZONTAL_FACING, direction).get());
        }
    }

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Direction face = context.requireContext(ContextKeys.INTERACTION_FACE).getOpposite();
        final LanternPlayer player = (LanternPlayer) context.getContext(ContextKeys.PLAYER).orElse(null);
        BlockSnapshotBuilder builder = null;
        Location location = context.requireContext(ContextKeys.INTERACTION_LOCATION);
        // Check if the block can be replaced
        if (location.getWorld().getProperty(location.getBlockPosition(), BlockProperties.IS_REPLACEABLE).get()) {
            builder = tryPlaceAt(player, location);
        } else {
            final Location relLocation = location.relativeToBlock(face.getOpposite());
            if (relLocation.getWorld().getProperty(relLocation.getBlockPosition(), BlockProperties.IS_REPLACEABLE).get()) {
                // Check if the clicked face is solid, if so, place the block there
                if (face != Direction.UP && location.getWorld().getProperty(
                        location.getBlockPosition(), face, BlockProperties.IS_REPLACEABLE).get()) {
                    builder = createBuilder(face.getOpposite());
                } else {
                    // Use the first valid face
                    builder = tryPlaceAt(player, relLocation);
                }
                location = relLocation;
            }
        }
        if (builder != null) {
            context.addBlockChange(builder.location(location).build());
            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.FAIL;
    }
}
