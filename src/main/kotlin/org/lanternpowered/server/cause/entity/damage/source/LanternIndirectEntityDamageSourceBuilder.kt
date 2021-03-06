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
package org.lanternpowered.server.cause.entity.damage.source

import org.spongepowered.api.entity.Entity
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource

class LanternIndirectEntityDamageSourceBuilder : AbstractEntityDamageSourceBuilder<IndirectEntityDamageSource, IndirectEntityDamageSource.Builder>(),
        IndirectEntityDamageSource.Builder {

    internal var indirect: Entity? = null

    override fun proxySource(proxy: Entity): IndirectEntityDamageSource.Builder = apply { this.indirect = proxy }

    override fun from(value: IndirectEntityDamageSource): IndirectEntityDamageSource.Builder = apply {
        super.from(value)
        this.indirect = value.indirectSource
    }

    override fun reset(): IndirectEntityDamageSource.Builder = apply {
        super.reset()
        this.indirect = null
    }

    override fun build(): IndirectEntityDamageSource {
        checkNotNull(this.damageType) { "The damage type must be set" }
        checkNotNull(this.source) { "The entity must be set" }
        checkNotNull(this.indirect) { "The proxy source must be set" }
        return LanternIndirectEntityDamageSource(this)
    }
}
