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

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.List;

public class SimpleBlockDropsProviderBehavior extends AbstractBlockDropsProviderBehavior {

    private final List<ItemStackSnapshot> itemStackSnapshots;

    public SimpleBlockDropsProviderBehavior(ItemStackSnapshot... itemStackSnapshots) {
        this.itemStackSnapshots = ImmutableList.copyOf(itemStackSnapshots);
    }

    @Override
    protected void collectDrops(BehaviorContext context, List<ItemStackSnapshot> itemStacks) {
        itemStacks.addAll(this.itemStackSnapshots);
    }
}
