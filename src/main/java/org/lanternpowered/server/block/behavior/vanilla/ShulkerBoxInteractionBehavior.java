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

import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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
    protected boolean validateOpenableSpace(BehaviorContext context, Location<World> location, List<Runnable> tasks) {
        final Direction facing = location.getBlock().getTraitValue(LanternEnumTraits.FACING).get();
        final Location<World> relLocation = location.getBlockRelative(facing);
        final AABB aabb = relLocation.getExtent().getBlockSelectionBox(
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
