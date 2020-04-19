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
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.block.provider.BlockObjectProvider;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.lanternpowered.server.world.extent.IExtent;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.Location;

import java.util.Collection;
import java.util.Optional;

public class PlacementCollisionDetectionBehavior implements PlaceBlockBehavior, InteractWithItemBehavior {

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Collection<BlockSnapshot> snapshots = context.getBlockSnapshots();
        for (BlockSnapshot snapshot : snapshots) {
            final Optional<Location> optLoc = snapshot.getLocation();
            if (!optLoc.isPresent()) {
                continue;
            }
            final Location loc = optLoc.get();
            final BlockState blockState = snapshot.getState();
            final BlockObjectProvider<Collection<AABB>> collisionBoxesProvider =
                    ((LanternBlockType) blockState.getType()).getCollisionBoxesProvider();
            if (collisionBoxesProvider != null) {
                final Collection<AABB> collisionBoxes = collisionBoxesProvider.get(blockState, null, null);
                for (AABB collisionBox : collisionBoxes) {
                    if (((IExtent) loc.getWorld()).hasIntersectingEntities(collisionBox.offset(loc.getBlockPosition()),
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
