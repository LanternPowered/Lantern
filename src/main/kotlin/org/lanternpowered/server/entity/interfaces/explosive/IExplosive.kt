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
package org.lanternpowered.server.entity.interfaces.explosive

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.entity.interfaces.IEntity
import org.lanternpowered.api.entity.shard.ExplosiveShard
import org.lanternpowered.api.entity.shard.FusedExplosiveShard
import org.spongepowered.api.entity.explosive.Explosive
import org.spongepowered.api.entity.explosive.FusedExplosive
import org.spongepowered.api.entity.explosive.PrimedTNT
import org.spongepowered.api.entity.living.Living

interface IExplosive : Explosive, IEntity {

    @JvmDefault override fun detonate() {
        getShard<ExplosiveShard>()?.detonate()
    }
}

interface IFusedExplosive : IExplosive, FusedExplosive {

    @JvmDefault override fun isPrimed() = getShardOfType<FusedExplosiveShard>()?.primed ?: false

    @JvmDefault override fun prime() {
        getShardOfType<FusedExplosiveShard>()?.prime()
    }

    @JvmDefault override fun defuse() {
        getShardOfType<FusedExplosiveShard>()?.defuse()
    }
}


interface IPrimedTNT : IFusedExplosive, PrimedTNT {

    @JvmDefault override fun getDetonator() = (getShard<ExplosiveShard>()?.detonator as? Living).optional()
}
