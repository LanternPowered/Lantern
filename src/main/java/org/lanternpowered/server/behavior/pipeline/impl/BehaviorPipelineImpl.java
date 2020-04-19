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

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.SubBehaviorPipeline;

import java.util.List;

public class BehaviorPipelineImpl<B extends Behavior> implements BehaviorPipeline<B> {

    final Class<B> behaviorType;
    final List<B> behaviors;

    final LoadingCache<Class<?>, SubBehaviorPipeline<?>> pipelineCache = Caffeine.newBuilder().build(this::load);

    SubBehaviorPipeline<?> load(Class<?> type) {
        //noinspection unchecked
        return new SubBehaviorPipelineImpl((Class) this.behaviorType, (List) this.behaviors.stream()
                .filter(type::isInstance).collect(ImmutableList.toImmutableList()), this);
    }

    public BehaviorPipelineImpl(Class<B> behaviorType, List<B> behaviors) {
        this.behaviorType = behaviorType;
        this.behaviors = behaviors;
    }

    @Override
    public List<B> getBehaviors() {
        return this.behaviors;
    }

    @Override
    public <S extends B> SubBehaviorPipeline<S> pipeline(Class<S> behaviorType) {
        checkNotNull(behaviorType, "behaviorType");
        //noinspection unchecked
        return (SubBehaviorPipeline<S>) this.pipelineCache.get(behaviorType);
    }
}
