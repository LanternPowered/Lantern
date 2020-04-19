/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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