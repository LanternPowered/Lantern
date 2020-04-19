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
import org.lanternpowered.api.util.collections.asUnmodifiableCollection
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.server.entity.LanternEntity

internal class LanternEntityEffectCollection(val effects: Multimap<EntityEffectType, EntityEffect>) : EntityEffectCollection {

    private val combinedEffects: MutableMap<EntityEffectType, EntityEffect?> = mutableMapOf()

    override fun getAll(effectType: EntityEffectType): Collection<EntityEffect> =
            this.effects[effectType].asUnmodifiableCollection()

    override fun getCombined(effectType: EntityEffectType): EntityEffect? {
        return this.combinedEffects.computeIfAbsent(effectType) { type: EntityEffectType ->
            val effects = this.effects[type].toImmutableList()
            if (effects.isNotEmpty()) {
                entityEffect { entity: LanternEntity ->
                    for (effect in effects) {
                        effect.play(entity)
                    }
                }
            } else emptyEntityEffect()
        }
    }

    override fun add(effectType: EntityEffectType, entityEffect: EntityEffect): Boolean {
        if (this.effects.put(effectType, entityEffect)) {
            // Invalidate
            this.combinedEffects.remove(effectType)
            return true
        }
        return false
    }

    override fun remove(effectType: EntityEffectType, entityEffect: EntityEffect): Boolean {
        if (this.effects.remove(effectType, entityEffect)) {
            // Invalidate
            this.combinedEffects.remove(effectType)
            return true
        }
        return false
    }

    override fun copy(): EntityEffectCollection = LanternEntityEffectCollection(HashMultimap.create(this.effects))
}