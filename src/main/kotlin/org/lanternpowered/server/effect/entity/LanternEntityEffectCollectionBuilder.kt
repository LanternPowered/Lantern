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