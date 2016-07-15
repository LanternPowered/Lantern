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
