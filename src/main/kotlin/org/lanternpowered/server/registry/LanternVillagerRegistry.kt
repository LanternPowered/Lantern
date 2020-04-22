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

import com.google.common.collect.Multimap
import org.lanternpowered.api.registry.VillagerRegistry
import org.spongepowered.api.data.type.Profession
import org.spongepowered.api.item.merchant.TradeOfferListMutator

object LanternVillagerRegistry : VillagerRegistry {

    override fun setMutators(profession: Profession, level: Int, mutators: MutableList<TradeOfferListMutator>): VillagerRegistry {
        TODO("Not yet implemented")
    }

    override fun setMutators(profession: Profession, mutatorMap: Multimap<Int, TradeOfferListMutator>): VillagerRegistry {
        TODO("Not yet implemented")
    }

    override fun addMutator(profession: Profession, level: Int, mutator: TradeOfferListMutator): VillagerRegistry {
        TODO("Not yet implemented")
    }

    override fun addMutators(
            profession: Profession, level: Int, mutator: TradeOfferListMutator, vararg mutators: TradeOfferListMutator): VillagerRegistry {
        TODO("Not yet implemented")
    }

    override fun getTradeOfferLevelMap(profession: Profession): Multimap<Int, TradeOfferListMutator> {
        TODO("Not yet implemented")
    }
}
