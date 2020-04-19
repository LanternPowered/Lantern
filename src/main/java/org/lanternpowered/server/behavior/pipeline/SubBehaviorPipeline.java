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

public interface SubBehaviorPipeline<B extends Behavior> extends BehaviorPipeline<B> {

    BehaviorPipeline<Behavior> parent();

    @Override
    default <S extends B> SubBehaviorPipeline<S> pipeline(Class<S> behaviorType) {
        return parent().pipeline(behaviorType);
    }

    <S extends B> SubBehaviorPipeline<S> subPipeline(Class<S> behaviorType);
}
