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
package org.lanternpowered.server.effect.entity

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap

class LanternEntityEffectCollectionBuilder : EntityEffectCollection.Builder {

    private val effects: Multimap<EntityEffectType, EntityEffect> = HashMultimap.create()

    override fun add(effectType: EntityEffectType, entityEffect: EntityEffect): EntityEffectCollection.Builder = apply {
        this.effects.put(effectType, entityEffect)
    }

    override fun addAll(entries: Map<EntityEffectType, EntityEffect>): EntityEffectCollection.Builder = apply {
        for ((key, value) in entries)
            this.effects.put(key, value)
    }

    override fun replaceOrAdd(effectType: EntityEffectType, effectToReplace: Class<out EntityEffect>, entityEffect: EntityEffect):
            EntityEffectCollection.Builder = apply {
        val effects = this.effects[effectType]
        effects.removeIf { obj: EntityEffect? -> effectToReplace.isInstance(obj) }
        effects.add(entityEffect)
    }

    override fun reset(effectType: EntityEffectType): EntityEffectCollection.Builder = apply {
        this.effects.removeAll(effectType)
    }

    override fun removeIf(effectType: EntityEffectType, predicate: (EntityEffect) -> Boolean): EntityEffectCollection.Builder = apply {
        this.effects[effectType].removeIf(predicate)
    }

    override fun build(): EntityEffectCollection = LanternEntityEffectCollection(HashMultimap.create(this.effects))

    override fun from(value: EntityEffectCollection): EntityEffectCollection.Builder = apply {
        this.effects.clear()
        this.effects.putAll((value as LanternEntityEffectCollection).effects)
    }

    override fun reset(): EntityEffectCollection.Builder = apply {
        this.effects.clear()
    }
}