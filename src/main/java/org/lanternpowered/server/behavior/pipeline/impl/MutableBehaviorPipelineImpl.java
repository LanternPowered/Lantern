/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.behavior.pipeline.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline;
import org.lanternpowered.server.behavior.pipeline.SubBehaviorPipeline;
import org.spongepowered.api.util.GuavaCollectors;

import java.util.List;

import javax.annotation.Nullable;

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
                .filter(type::isInstance).collect(GuavaCollectors.toImmutableList()), this);
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
