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

import java.util.List;

public interface BehaviorPipeline<B extends Behavior> {

    /**
     * Gets all the {@link Behavior}s in this pipeline.
     *
     * @return The block behaviors
     */
    List<B> getBehaviors();

    /**
     * Gets a {@link SubBehaviorPipeline} with all the behaviors of the specific type.
     *
     * @param behaviorType The behavior type
     * @param <S> The sub behavior type
     * @return The pipeline
     */
    <S extends B> SubBehaviorPipeline<S> pipeline(Class<S> behaviorType);
}
