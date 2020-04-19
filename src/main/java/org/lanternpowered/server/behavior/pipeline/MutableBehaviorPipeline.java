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
package org.lanternpowered.server.behavior.pipeline;

import org.lanternpowered.server.behavior.Behavior;

public interface MutableBehaviorPipeline<B extends Behavior> extends BehaviorPipeline<B> {

    MutableBehaviorPipeline<B> add(B blockBehavior);

    MutableBehaviorPipeline<B> insert(int index, B blockBehavior);
}
