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

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.SubBehaviorPipeline;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public class MutableBehaviorPipelineImpl<B extends Behavior> extends BehaviorPipelineImpl<B>
        implements MutableBehaviorPipeline<B> {

    @Nullable private volatile ImmutableList<B> immutableBehaviors;

    public MutableBehaviorPipelineImpl(Class<B> behaviorType, List<B> behaviors) {
        super(behaviorType, behaviors);
    }

    @Override
    SubBehaviorPipeline<?> load(Class<?> type) {
        //noinspection unchecked
        return new SubBehaviorPipelineImpl((Class) this.behaviorType, (List) getBehaviors().stream()
                .filter(type::isInstance).collect(ImmutableList.toImmutableList()), this);
    }

    @Override
    public List<B> getBehaviors() {
        ImmutableList<B> immutableBehaviors = this.immutableBehaviors;
        if (immutableBehaviors == null) {
            synchronized (this.behaviors) {
                immutableBehaviors = this.immutableBehaviors = ImmutableList.copyOf(this.behaviors);
            }
        }
        return immutableBehaviors;
    }

    @Override
    public MutableBehaviorPipelineImpl<B> add(B blockBehavior) {
        checkNotNull(blockBehavior, "blockBehavior");
        synchronized (this.behaviors) {
            this.behaviors.add(blockBehavior);
        }
        this.immutableBehaviors = null;
        // Refresh the cache for all the subtypes for block sub behaviors
        TypeToken.of(blockBehavior.getClass()).getTypes().rawTypes().forEach(this.pipelineCache::refresh);
        return this;
    }

    @Override
    public MutableBehaviorPipelineImpl<B> insert(int index, B blockBehavior) {
        checkNotNull(blockBehavior, "blockBehavior");
        if (index < 0) {
            index = 0;
        }
        synchronized (this.behaviors) {
            if (index >= this.behaviors.size()) {
                this.behaviors.add(blockBehavior);
            } else {
                this.behaviors.add(index, blockBehavior);
            }
        }
        this.immutableBehaviors = null;
        // Refresh the cache for all the subtypes for block sub behaviors
        TypeToken.of(blockBehavior.getClass()).getTypes().rawTypes().forEach(this.pipelineCache::refresh);
        return this;
    }
}
