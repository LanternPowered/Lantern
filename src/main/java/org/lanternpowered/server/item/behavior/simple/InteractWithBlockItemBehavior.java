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
package org.lanternpowered.server.item.behavior.simple;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;

@SuppressWarnings("ConstantConditions")
public class InteractWithBlockItemBehavior extends InteractWithBlockItemBaseBehavior {

    @Override
    protected boolean place(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final LanternBlockType blockType = (LanternBlockType) context.getContext(ContextKeys.ITEM_TYPE).get().getBlock().get();
        context.addContext(ContextKeys.BLOCK_TYPE, blockType);
        return context.process(blockType.getPipeline().pipeline(PlaceBlockBehavior.class),
                (context1, behavior1) -> behavior1.tryPlace(pipeline, context1)).isSuccess();
    }
}
