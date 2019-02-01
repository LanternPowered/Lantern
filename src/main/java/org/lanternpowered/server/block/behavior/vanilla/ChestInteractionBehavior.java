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
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ChestInteractionBehavior extends OpenableContainerInteractionBehavior {

    @Override
    protected Optional<Inventory> getInventoryFrom(TileEntity tileEntity) {
        if (tileEntity instanceof Chest) {
            final Optional<Inventory> optDouble = ((Chest) tileEntity).getDoubleChestInventory();
            if (optDouble.isPresent()) {
                return optDouble;
            }
        }
        return super.getInventoryFrom(tileEntity);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected boolean validateOpenableSpace(BehaviorContext context, Location location, List<Runnable> tasks) {
        final TileEntity tileEntity = location.getTileEntity().get();
        if (tileEntity instanceof Chest) {
            final Set<Chest> chests = new HashSet<>(((Chest) tileEntity).getConnectedChests());
            chests.add((Chest) tileEntity);
            for (Chest chest : chests) {
                final Location loc = chest.getLocation();
                if (!validateOpenableChestSpace(context, loc, tasks)) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean validateOpenableChestSpace(BehaviorContext context, Location loc, List<Runnable> tasks) {
        final Location relLoc = loc.getBlockRelative(Direction.UP);
        final AABB relAabb = relLoc.getWorld().getBlockSelectionBox(
                relLoc.getBlockPosition()).orElse(null);
        if (relAabb != null) {
            AABB aabb = loc.getWorld().getBlockSelectionBox(
                    loc.getBlockPosition()).get();
            aabb = aabb.offset(0, 1, 0);
            aabb = new AABB(aabb.getMin(), aabb.getMax().mul(1, 0, 1)
                    .add(0, aabb.getMin().getY() + 0.43, 0));
            if (aabb.intersects(relAabb)) {
                final ReplaceableProperty replaceableProperty = relLoc
                        .getProperty(ReplaceableProperty.class).orElse(null);
                // Replaceable blocks will be replaced when opened
                if (replaceableProperty != null && replaceableProperty.getValue()) {
                    tasks.add(() -> context.addBlockChange(BlockSnapshotBuilder.create()
                            .location(relLoc)
                            .blockState(BlockTypes.AIR.getDefaultState())
                            .build()));
                    // TODO: Use break block pipeline instead
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
