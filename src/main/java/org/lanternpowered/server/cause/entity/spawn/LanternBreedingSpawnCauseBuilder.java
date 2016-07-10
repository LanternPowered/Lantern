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
package org.lanternpowered.server.cause.entity.spawn;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.entity.spawn.BreedingSpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.common.AbstractEntitySpawnCauseBuilder;

public class LanternBreedingSpawnCauseBuilder extends AbstractEntitySpawnCauseBuilder<BreedingSpawnCause, BreedingSpawnCause.Builder>
        implements BreedingSpawnCause.Builder {

    protected Entity mate;

    @Override
    public BreedingSpawnCause.Builder mate(Entity entity) {
        this.mate = checkNotNull(entity, "entity");
        return this;
    }

    @Override
    public BreedingSpawnCause.Builder from(BreedingSpawnCause value) {
        this.mate = value.getMate();
        return super.from(value);
    }

    @Override
    public BreedingSpawnCause.Builder reset() {
        this.mate = null;
        return super.reset();
    }

    @Override
    public BreedingSpawnCause build() {
        checkState(this.spawnType != null, "The spawn type must be set");
        checkState(this.entity != null, "The entity must be set");
        checkState(this.mate != null, "The mate entity must be set");
        return new LanternBreedingSpawnCause(this);
    }
}
