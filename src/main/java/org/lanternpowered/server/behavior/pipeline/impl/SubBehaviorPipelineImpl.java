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
package org.lanternpowered.server.behavior.pipeline.impl;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.SubBehaviorPipeline;

import java.util.List;

public class SubBehaviorPipelineImpl<B extends Behavior> extends BehaviorPipelineImpl<B> implements SubBehaviorPipeline<B> {

    private final BehaviorPipeline<Behavior> parent;

    public SubBehaviorPipelineImpl(Class<B> behaviorType, List<B> behaviors, BehaviorPipeline<Behavior> parent) {
        super(behaviorType, behaviors);
        this.parent = parent;
    }

    @Override
    public BehaviorPipeline<Behavior> parent() {
        return this.parent;
    }

    @Override
    public <S extends B> SubBehaviorPipeline<S> pipeline(Class<S> behaviorType) {
        return SubBehaviorPipeline.super.pipeline(behaviorType);
    }

    @Override
    public <S extends B> SubBehaviorPipeline<S> subPipeline(Class<S> behaviorType) {
        return super.pipeline(behaviorType);
    }
}
