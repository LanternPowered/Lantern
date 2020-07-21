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
package org.lanternpowered.server.registry

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import org.lanternpowered.api.registry.VillagerRegistry
import org.spongepowered.api.data.type.ProfessionType
import org.spongepowered.api.item.merchant.TradeOfferListMutator

object LanternVillagerRegistry : VillagerRegistry {

    private val multimap = HashMultimap.create<Int, TradeOfferListMutator>()
    private var cached: Multimap<Int, TradeOfferListMutator>? = null

    override fun setMutators(profession: ProfessionType, level: Int, mutators: List<TradeOfferListMutator>): VillagerRegistry = apply {
        this.multimap.removeAll(level)
        this.multimap.putAll(level, mutators)
        this.cached = null
    }

    override fun setMutators(profession: ProfessionType, mutatorMap: Multimap<Int, TradeOfferListMutator>): VillagerRegistry = apply {
        this.multimap.clear()
        this.multimap.putAll(mutatorMap)
        this.cached = null
    }

    override fun addMutator(profession: ProfessionType, level: Int, mutator: TradeOfferListMutator): VillagerRegistry = apply {
        this.multimap.put(level, mutator)
        this.cached = null
    }

    override fun addMutators(profession: ProfessionType, level: Int, mutator: TradeOfferListMutator, vararg mutators: TradeOfferListMutator):
            VillagerRegistry = apply {
        addMutators(profession, level, mutator)
        mutators.forEach { addMutators(profession, level, it) }
    }

    override fun getTradeOfferLevelMap(profession: ProfessionType): Multimap<Int, TradeOfferListMutator> {
        var cached = this.cached
        if (cached != null)
            return cached
        cached = ImmutableMultimap.copyOf(this.multimap)
        this.cached = cached
        return cached
    }
}
