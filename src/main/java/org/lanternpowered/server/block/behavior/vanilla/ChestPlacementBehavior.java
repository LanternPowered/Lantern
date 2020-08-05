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
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.block.state.BlockStateProperties;
import org.lanternpowered.server.data.type.LanternChestAttachmentType;
import org.lanternpowered.server.entity.player.LanternPlayer;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.Arrays;
import java.util.List;

public class ChestPlacementBehavior implements PlaceBlockBehavior {

    private static final List<Direction> EW = Arrays.asList(Direction.EAST, Direction.WEST);
    private static final List<Direction> WE = Arrays.asList(Direction.WEST, Direction.EAST);
    private static final List<Direction> NS = Arrays.asList(Direction.NORTH, Direction.SOUTH);
    private static final List<Direction> SN = Arrays.asList(Direction.SOUTH, Direction.NORTH);

    private static List<Direction> getConnectionDirections(Direction direction) {
        switch (direction) {
            case NORTH:
                return EW;
            case SOUTH:
                return WE;
            case EAST:
                return SN;
            case WEST:
                return NS;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final BlockSnapshot snapshot = context.getContext(ContextKeys.BLOCK_SNAPSHOT)
                .orElseThrow(() -> new IllegalStateException("The BlockSnapshotProviderPlaceBehavior's BlockSnapshot isn't present."));
        final Location location = context.requireContext(ContextKeys.BLOCK_LOCATION);

        final BlockType blockType = context.requireContext(ContextKeys.BLOCK_TYPE);
        final Direction face = context.requireContext(ContextKeys.INTERACTION_FACE);

        final LanternPlayer player = (LanternPlayer) context.getContext(ContextKeys.PLAYER).orElse(null);
        // Get the direction the chest should face
        Direction facing;
        if (player != null) {
            facing = player.getHorizontalDirection(Direction.Division.CARDINAL).getOpposite();
        } else {
            facing = Direction.NORTH;
        }

        LanternChestAttachmentType connection = null;
        Direction direction = null;

        boolean sneaking = false;
        if (player != null && player.get(Keys.IS_SNEAKING).get()) {
            sneaking = true;
        }

        Location relLocation = location.getBlockRelative(face.getOpposite());
        BlockState relState = relLocation.getBlock();
        if (relState.getType() == blockType) {
            final LanternChestAttachmentType relConnection = relState.getTraitValue(BlockStateProperties.CHEST_ATTACHMENT_TYPE).get();
            if (relConnection == LanternChestAttachmentType.SINGLE) {
                final Direction relFacing = relState.getTraitValue(BlockStateProperties.HORIZONTAL_FACING).get();
                final List<Direction> dirs = getConnectionDirections(relFacing);
                int index = dirs.indexOf(face);
                if (index-- != -1) {
                    connection = index == 0 ? LanternChestAttachmentType.LEFT : LanternChestAttachmentType.RIGHT;
                    direction = relFacing;
                    context.addBlockChange(BlockSnapshot.builder()
                            .from(relLocation.createSnapshot())
                            .add(Keys.CHEST_ATTACHMENT, index == 0 ? LanternChestAttachmentType.RIGHT : LanternChestAttachmentType.LEFT)
                            .build());
                }
            } else {
                sneaking = true;
            }
        }
        if (connection == null) {
            direction = facing;
            if (!sneaking) {
                final List<Direction> dirs = getConnectionDirections(direction);
                int index = 0;
                for (Direction relDirection : dirs) {
                    relLocation = location.getBlockRelative(relDirection);
                    relState = relLocation.getBlock();
                    if (relState.getType() == blockType) {
                        final LanternChestAttachmentType relConnection = relState.getTraitValue(BlockStateProperties.CHEST_ATTACHMENT_TYPE).get();
                        final Direction relFacing = relState.getTraitValue(BlockStateProperties.HORIZONTAL_FACING).get();
                        if (relFacing == facing && relConnection == LanternChestAttachmentType.SINGLE) {
                            connection = index == 0 ? LanternChestAttachmentType.LEFT : LanternChestAttachmentType.RIGHT;
                            context.addBlockChange(BlockSnapshot.builder()
                                    .from(relLocation.createSnapshot())
                                    .add(Keys.CHEST_ATTACHMENT, index == 0 ? LanternChestAttachmentType.RIGHT : LanternChestAttachmentType.LEFT)
                                    .build());
                            break;
                        }
                    }
                    index++;
                }
            }
            if (connection == null) {
                connection = LanternChestAttachmentType.SINGLE;
            }
        }

        context.addBlockChange(BlockSnapshotBuilder.create().from(snapshot).location(location)
                .add(Keys.DIRECTION, direction)
                .add(Keys.CHEST_ATTACHMENT, connection)
                .build());
        return BehaviorResult.SUCCESS;
    }
}
