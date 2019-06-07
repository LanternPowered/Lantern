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
        final Location location = context.getContext(ContextKeys.BLOCK_LOCATION).get();

        final BlockState baseState = location.getBlock();
        final LanternDoorHalf half = baseState.get(LanternKeys.DOOR_HALF).get();

        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.create();
        builder.blockState(BlockTypes.AIR.getDefaultState());
        context.populateBlockSnapshot(builder, BehaviorContext.PopulationFlags.CREATOR_AND_NOTIFIER);

        builder.location(location);
        context.addBlockChange(builder.build());

        final Direction dir = half == LanternDoorHalf.LOWER ? Direction.UP : Direction.DOWN;
        final LanternDoorHalf other = half == LanternDoorHalf.LOWER ? LanternDoorHalf.UPPER : LanternDoorHalf.LOWER;
        final Location loc = location.getBlockRelative(dir);

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
