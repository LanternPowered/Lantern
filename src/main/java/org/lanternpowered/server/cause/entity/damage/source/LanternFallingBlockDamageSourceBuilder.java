/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.cause.entity.damage.source;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFallingBlockData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.common.AbstractEntityDamageSource.AbstractEntityDamageSourceBuilder;

public class LanternFallingBlockDamageSourceBuilder extends AbstractEntityDamageSourceBuilder<FallingBlockDamageSource,
        FallingBlockDamageSource.Builder> implements FallingBlockDamageSource.Builder {

    protected ImmutableFallingBlockData fallingBlockData;

    @Override
    public FallingBlockDamageSource.Builder entity(Entity entity) {
        checkNotNull(entity, "Entity source cannot be null!");
        checkArgument(entity instanceof FallingBlock, "Entity source must be a falling block!");
        return super.entity(entity);
    }

    @Override
    public FallingBlockDamageSource.Builder fallingBlock(ImmutableFallingBlockData fallingBlockData) {
        this.fallingBlockData = checkNotNull(fallingBlockData, "fallingBlockData");
        return this;
    }

    @Override
    public FallingBlockDamageSource build() throws IllegalStateException {
        checkState(this.source != null, "The falling block entity must be set");
        ImmutableFallingBlockData fallingBlockData = this.fallingBlockData;
        if (fallingBlockData == null && this.source != null) {
            fallingBlockData = ((FallingBlock) this.source).getFallingBlockData().asImmutable();
        }
        return new LanternFallingBlockDamageSource(this, fallingBlockData);
    }
}
