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

import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.state.BlockStateProperties;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.List;

public class ShulkerBoxInteractionBehavior extends OpenableContainerInteractionBehavior {

    private static final AABB NORTH = new AABB(0, 0, 0.51, 1, 1, 1);
    private static final AABB SOUTH = new AABB(0, 0, 0, 1, 1, 0.49);
    private static final AABB EAST = new AABB(0, 0, 0, 0.49, 1, 1);
    private static final AABB WEST = new AABB(0.51, 0, 0, 1, 1, 1);
    private static final AABB UP = new AABB(0, 0, 0, 1, 0.49, 1);
    private static final AABB DOWN = new AABB(0, 0.51, 0, 1, 1, 1);

    private static AABB getExtendedAABB(Direction direction) {
        switch (direction) {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            case SOUTH:
                return SOUTH;
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected boolean validateOpenableSpace(BehaviorContext context, Location location, List<Runnable> tasks) {
        final Direction facing = location.getBlock().getTraitValue(BlockStateProperties.FACING).get();
        final Location relLocation = location.getBlockRelative(facing);
        final AABB aabb = relLocation.getWorld().getBlockSelectionBox(
                relLocation.getBlockPosition()).orElse(null);
        if (aabb != null && getExtendedAABB(facing).offset(
                relLocation.getBlockPosition()).intersects(aabb)) {
            final ReplaceableProperty replaceableProperty = relLocation.getProperty(ReplaceableProperty.class).orElse(null);
            // Replaceable blocks will be replaced when opened
            if (replaceableProperty != null && replaceableProperty.getValue()) {
                tasks.add(() -> context.addBlockChange(BlockSnapshotBuilder.create()
                        .location(relLocation)
                        .blockState(BlockTypes.AIR.getDefaultState())
                        .build()));
                // TODO: Use break block pipeline instead
                return true;
            }
            return false;
        }
        return true;
    }
}
