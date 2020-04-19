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
package org.lanternpowered.server.block.behavior.types;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.spongepowered.api.block.BlockType;

/**
 * This behavior is triggered when a {@link BlockType}
 * is being placed.
 */
public interface PlaceBlockBehavior extends Behavior {

    /**
     * Tries to place the block for the context.
     *
     * @param context The context
     * @return The interaction result
     */
    BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context);
}
