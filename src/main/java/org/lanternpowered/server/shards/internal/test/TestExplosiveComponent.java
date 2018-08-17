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
package org.lanternpowered.server.shards.internal.test;

import com.google.inject.Inject;
import org.lanternpowered.api.entity.shard.AIShard;
import org.lanternpowered.api.entity.shard.ExplosiveShard;
import org.lanternpowered.server.shards.Holder;
import org.lanternpowered.server.shards.Opt;
import org.spongepowered.api.entity.Entity;

import java.util.Optional;

public class TestExplosiveComponent extends ExplosiveShard {

    // Fields will be made public by class transformation

    @Inject private Opt<AIShard> aiComponentOpt; // If it would be useful...

    // Inject the holder
    @Holder public Entity entity;

    @Override
    public Optional<Entity> getDetonator() {
        return Optional.empty();
    }

    @Override
    public void detonate() {
        System.out.println("BOOM!");
    }

    // Start generated stuff

    // The lock of this component
    public final Object $shards_internal_lock = new Object();

    @Inject
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    // The holder, only used if there isn't a @Holder field
    // public ComponentHolder $shards_internal_holder;

}
