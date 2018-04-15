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
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.block.provider.ObjectProvider;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.lanternpowered.server.world.extent.IExtent;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

public class PlacementCollisionDetectionBehavior implements PlaceBlockBehavior, InteractWithItemBehavior {

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Collection<BlockSnapshot> snapshots = context.getBlockSnapshots();
        for (BlockSnapshot snapshot : snapshots) {
            final Optional<Location<World>> optLoc = snapshot.getLocation();
            if (!optLoc.isPresent()) {
                continue;
            }
            final Location<World> loc = optLoc.get();
            final BlockState blockState = snapshot.getState();
            final ObjectProvider<Collection<AABB>> collisionBoxesProvider =
                    ((LanternBlockType) blockState.getType()).getCollisionBoxesProvider();
            if (collisionBoxesProvider != null) {
                final Collection<AABB> collisionBoxes = collisionBoxesProvider.get(blockState, null, null);
                for (AABB collisionBox : collisionBoxes) {
                    if (((IExtent) loc.getExtent()).hasIntersectingEntities(collisionBox.offset(loc.getBlockPosition()),
                            entity -> !(entity instanceof Item || entity instanceof ExperienceOrb))) { // TODO: Configure this filter somewhere?
                        return BehaviorResult.FAIL;
                    }
                }
            }
        }
        return BehaviorResult.CONTINUE;
    }

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        return tryPlace(pipeline, context);
    }
}
