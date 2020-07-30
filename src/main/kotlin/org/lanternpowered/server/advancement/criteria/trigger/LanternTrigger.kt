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
package org.lanternpowered.server.advancement.criteria.trigger

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.gson.JsonObject
import org.lanternpowered.api.Server
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.event.LanternEventFactory
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.advancement.LanternPlayerAdvancements
import org.lanternpowered.server.advancement.criteria.progress.AbstractCriterionProgress
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.entity.living.player.LanternPlayer
import org.spongepowered.api.advancement.criteria.AdvancementCriterion
import org.spongepowered.api.advancement.criteria.ScoreCriterionProgress
import org.spongepowered.api.advancement.criteria.trigger.FilteredTrigger
import org.spongepowered.api.advancement.criteria.trigger.FilteredTriggerConfiguration
import org.spongepowered.api.advancement.criteria.trigger.Trigger
import org.spongepowered.api.entity.living.player.server.ServerPlayer
import org.spongepowered.api.event.advancement.CriterionEvent

class LanternTrigger<C : FilteredTriggerConfiguration>(
        key: NamespacedKey,
        private val configType: TypeToken<C>,
        val configConstructor: (JsonObject) -> C,
        val eventHandler: ((CriterionEvent.Trigger<C>) -> Unit)?
) : DefaultCatalogType(key), Trigger<C> {

    // All the criteria progress that could be triggered by this trigger
    private val progress: Multimap<LanternPlayerAdvancements, AbstractCriterionProgress<*>> = HashMultimap.create()

    override fun getConfigurationType(): Class<C> = this.configType.rawType.uncheckedCast()

    override fun trigger() {
        this.trigger(Server.onlinePlayers)
    }

    override fun trigger(players: Iterable<ServerPlayer>) {
        for (player in players)
            this.trigger(player)
    }

    override fun trigger(player: ServerPlayer) {
        val playerAdvancements = (player as LanternPlayer).advancementsProgress
        val collection = this.progress[playerAdvancements]
        if (collection.isEmpty())
            return
        val cause = CauseStack.current().currentCause
        for (progress in this.progress[playerAdvancements].toList()) {
            val advancement = progress.advancementProgress.advancement
            val criterion: AdvancementCriterion = progress.criterion
            val filteredTrigger: FilteredTrigger<C> = criterion.trigger.get().uncheckedCast()
            val eventHandler = this.eventHandler
            val event: CriterionEvent.Trigger<C> = LanternEventFactory.createCriterionEventTrigger(
                    cause, advancement, criterion, player, filteredTrigger, eventHandler != null)
            eventHandler?.invoke(event)
            EventManager.post(event)
            if (event.result) {
                if (progress is ScoreCriterionProgress) {
                    (progress as ScoreCriterionProgress).add(1)
                } else {
                    progress.grant()
                }
            }
        }
    }

    fun add(playerAdvancements: LanternPlayerAdvancements, criterionProgress: AbstractCriterionProgress<*>) {
        this.progress.put(playerAdvancements, criterionProgress)
    }

    fun remove(playerAdvancements: LanternPlayerAdvancements, criterionProgress: AbstractCriterionProgress<*>) {
        this.progress.remove(playerAdvancements, criterionProgress)
    }

    override fun toStringHelper(): ToStringHelper = super.toStringHelper()
            .add("configType", this.configType)
}
