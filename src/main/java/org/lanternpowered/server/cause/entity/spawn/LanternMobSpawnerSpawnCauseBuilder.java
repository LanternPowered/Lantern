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
package org.lanternpowered.server.cause.entity.spawn;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.data.manipulator.immutable.ImmutableMobSpawnerData;
import org.spongepowered.api.event.cause.entity.spawn.MobSpawnerSpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.common.AbstractSpawnCauseBuilder;

public class LanternMobSpawnerSpawnCauseBuilder extends AbstractSpawnCauseBuilder<MobSpawnerSpawnCause, MobSpawnerSpawnCause.Builder>
        implements MobSpawnerSpawnCause.Builder {

    protected ImmutableMobSpawnerData mobSpawnerData;

    @Override
    public MobSpawnerSpawnCause.Builder spawnerData(ImmutableMobSpawnerData spawnerData) {
        this.mobSpawnerData = checkNotNull(spawnerData, "spawnerData");
        return this;
    }

    @Override
    public MobSpawnerSpawnCause.Builder from(MobSpawnerSpawnCause value) {
        this.mobSpawnerData = value.getMobSpawnerData();
        return super.from(value);
    }

    @Override
    public MobSpawnerSpawnCause.Builder reset() {
        this.mobSpawnerData = null;
        return super.reset();
    }

    @Override
    public MobSpawnerSpawnCause build() {
        checkState(this.spawnType != null, "The spawn type must be set");
        checkState(this.mobSpawnerData != null, "The mob spawner data must be set");
        return new LanternMobSpawnerSpawnCause(this);
    }
}
