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
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.spongepowered.api.block.BlockSnapshot;

public class SimplePlacementBehavior implements PlaceBlockBehavior {

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final BlockSnapshot snapshot = context.getContext(ContextKeys.BLOCK_SNAPSHOT)
                .orElseThrow(() -> new IllegalStateException("The BlockSnapshotProviderPlaceBehavior's BlockSnapshot isn't present."));
        context.addBlockChange(BlockSnapshotBuilder.create().from(snapshot)
                .location(context.requireContext(ContextKeys.BLOCK_LOCATION)).build());
        return BehaviorResult.CONTINUE;
    }
}
