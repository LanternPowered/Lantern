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
package org.lanternpowered.server.block.behavior.simple;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.behavior.types.BlockDropsProviderBehavior;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBlockDropsProviderBehavior implements BlockDropsProviderBehavior {

    @Override
    public void tryAddDrops(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final List<ItemStackSnapshot> itemStacks = new ArrayList<>();
        collectDrops(context, itemStacks);
        final Location location = context.requireContext(ContextKeys.BLOCK_LOCATION);
        final Vector3d position = location.getPosition().add(0.5, 0.5, 0.5);
        itemStacks.forEach(itemStack -> {
            final Entity entity = location.getWorld().createEntity(EntityTypes.ITEM, position);
            entity.offer(Keys.REPRESENTED_ITEM, itemStack);
            context.addEntity(entity);
        });
    }

    protected abstract void collectDrops(BehaviorContext context, List<ItemStackSnapshot> itemStacks);
}
