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
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.behavior.types.BreakBlockBehavior;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternDoorHalf;
import org.lanternpowered.server.util.Quaternions;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.ServerLocation;
import org.spongepowered.math.imaginary.Quaterniond;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class DoorBehavior implements PlaceBlockBehavior, BreakBlockBehavior {

    private static final Quaterniond LEFT_ANGLE = Quaternions.fromAxesAnglesDeg(new Vector3d(0, -90, 0));

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Location location = context.requireContext(ContextKeys.BLOCK_LOCATION);
        final Direction face = context.requireContext(ContextKeys.INTERACTION_FACE);

        // Door can only be placed by clicking in the floor
        if (face != Direction.DOWN) {
            return BehaviorResult.PASS;
        }
        final Location down = location.getBlockRelative(Direction.DOWN);
        final SolidCubeProperty solidProp = down.getProperty(SolidCubeProperty.class).get();
        // The door must be placed on a solid block
        if (!solidProp.getValue()) {
            return BehaviorResult.PASS;
        }
        final Location up = location.getBlockRelative(Direction.UP);
        final ReplaceableProperty replaceableProp = up.getProperty(ReplaceableProperty.class).get();
        if (!replaceableProp.getValue()) {
            return BehaviorResult.PASS;
        }
        final BlockSnapshot snapshot = context.getContext(ContextKeys.BLOCK_SNAPSHOT)
                .orElseThrow(() -> new IllegalStateException("The BlockSnapshotRetrieveBehavior BlockSnapshot isn't present."));
        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.create().from(snapshot);
        context.populateBlockSnapshot(builder, BehaviorContext.PopulationFlags.CREATOR_AND_NOTIFIER);

        Direction facing = Direction.NORTH;
        Vector3i left = Vector3i.UNIT_X;
        final Optional<Entity> optSource = context.first(Entity.class);
        if (optSource.isPresent()) {
            final Entity source = optSource.get();
            final Vector3d rotVector;
            if (source instanceof Living) {
                rotVector = ((Living) source).getHeadRotation();
            } else {
                rotVector = optSource.get().getRotation();
            }

            // Calculate the direction the entity is looking
            final Vector3d dir = Quaternions.fromAxesAnglesDeg(rotVector.mul(-1)).rotate(Vector3d.FORWARD);
            facing = Direction.getClosestHorizontal(dir, Direction.Division.CARDINAL);
            left = LEFT_ANGLE.rotate(facing.asOffset()).toInt();
            facing = facing.getOpposite();
        }

        builder.add(Keys.DIRECTION, facing);
        // TODO: Hinges

        context.addBlockChange(builder.location(location).build());
        context.addBlockChange(builder.add(LanternKeys.DOOR_HALF, LanternDoorHalf.UPPER).location(up).build());
        return BehaviorResult.SUCCESS;
    }

    @Override
    public BehaviorResult tryBreak(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final ServerLocation location = context.getContext(ContextKeys.BLOCK_LOCATION).get();

        final BlockState baseState = location.getBlock();
        final LanternDoorHalf half = baseState.get(LanternKeys.DOOR_HALF).get();

        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.create();
        builder.blockState(BlockTypes.AIR.get().getDefaultState());
        context.populateBlockSnapshot(builder, BehaviorContext.PopulationFlags.CREATOR_AND_NOTIFIER);

        builder.location(location);
        context.addBlockChange(builder.build());

        final Direction dir = half == LanternDoorHalf.LOWER ? Direction.UP : Direction.DOWN;
        final LanternDoorHalf other = half == LanternDoorHalf.LOWER ? LanternDoorHalf.UPPER : LanternDoorHalf.LOWER;
        final ServerLocation loc = location.relativeToBlock(dir);

        BlockState otherState = loc.getBlock();
        if (otherState.get(LanternKeys.DOOR_HALF).orElse(null) == other &&
                otherState.with(LanternKeys.DOOR_HALF, half).orElse(null) == baseState) {
            builder.location(loc);
            context.addBlockChange(builder.build());
            return BehaviorResult.CONTINUE;
        }

        return BehaviorResult.CONTINUE;
    }
}
