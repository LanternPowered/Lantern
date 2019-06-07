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
package org.lanternpowered.server.block.behavior.simple;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.behavior.types.BlockDropsProviderBehavior;
import org.spongepowered.api.data.key.Keys;
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
