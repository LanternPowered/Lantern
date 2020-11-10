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
package org.lanternpowered.server.item.behavior.vanilla

import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.data.neq
import org.lanternpowered.api.effect.potion.mergeWith
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.entity.player.fix
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.fix
import org.lanternpowered.api.item.inventory.stack.isNotEmpty
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.BehaviorContext
import org.lanternpowered.server.behavior.BehaviorResult
import org.lanternpowered.server.behavior.ContextKeys
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline
import org.lanternpowered.server.item.behavior.types.FinishUsingItemBehavior
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.min

class ConsumableInteractionBehavior : InteractWithItemBehavior, FinishUsingItemBehavior {

    private var consumer: ((Player, BehaviorPipeline<Behavior>, BehaviorContext) -> Unit)? = null
    private var restItemSupplier: (() -> ItemStack)? = null

    override fun tryInteract(pipeline: BehaviorPipeline<Behavior>, context: BehaviorContext): BehaviorResult {
        val player = context[CauseContextKeys.PLAYER] ?: return BehaviorResult.PASS
        val itemStack: ItemStack = context.requireContext(ContextKeys.USED_ITEM_STACK)
        if (itemStack.get(Keys.IS_ALWAYS_CONSUMABLE).orElse(false)) {
            var status = 0
            val replenishedFood = itemStack.get(Keys.REPLENISHED_FOOD).orElse(0.0)
            if (replenishedFood != 0.0) {
                val maxFood = player.get(Keys.MAX_FOOD).orElse(1.0)
                val food = player.get(Keys.FOOD).orElse(maxFood)
                status = if (food < maxFood) 2 else 1
            }
            if (status != 2) {
                val restoredHealth: Double = itemStack.get(Keys.RESTORED_HEALTH).orElse(0.0)
                if (restoredHealth != 0.0) {
                    val maxHealth = player.get(Keys.MAX_HEALTH).orElse(1.0)
                    val health = player.get(Keys.HEALTH).orElse(maxHealth)
                    status = if (health < maxHealth) 2 else 1
                }
            }
            if (status == 1)
                return BehaviorResult.PASS
        }
        player.offer(Keys.ACTIVE_HAND, context.requireContext(ContextKeys.INTERACTION_HAND))
        return BehaviorResult.SUCCESS
    }

    override fun tryUse(pipeline: BehaviorPipeline<Behavior>, context: BehaviorContext): BehaviorResult {
        val player = context[CauseContextKeys.PLAYER] ?: return BehaviorResult.PASS
        player.fix()
        val itemStack = context.requireContext(ContextKeys.USED_ITEM_STACK)
        val replenishedFood = itemStack.get(Keys.REPLENISHED_FOOD).orElse(0.0)
        if (replenishedFood != 0.0) {
            val maxFood = player.get(Keys.MAX_FOOD).orElse(Double.MAX_VALUE)
            val food = player.get(Keys.FOOD).orNull()
            if (food != null)
                player.offer(Keys.FOOD, min(food + replenishedFood, maxFood ?: Double.MAX_VALUE))
        }
        val restoredHealth = itemStack.get(Keys.RESTORED_HEALTH).orElse(0.0)
        if (restoredHealth != 0.0) {
            val maxHealth = player.get(Keys.MAX_HEALTH).orElse(Double.MAX_VALUE)
            val health = player.get(Keys.HEALTH).orNull()
            if (health != null)
                player.offer(Keys.HEALTH, min(health + restoredHealth, maxHealth))
        }
        val replenishedSaturation = itemStack.get(Keys.REPLENISHED_SATURATION).orElse(0.0)
        if (replenishedSaturation != 0.0) {
            val saturation = player.get(Keys.SATURATION).orNull()
            if (saturation != null)
                player.offer(Keys.SATURATION, min(saturation + replenishedSaturation, player.get(Keys.FOOD).orElse(20.0)))
        }
        val applicablePotionEffects = itemStack.get(Keys.APPLICABLE_POTION_EFFECTS).orNull()
        if (applicablePotionEffects != null) {
            val effectsToApply = applicablePotionEffects.get(ThreadLocalRandom.current())
            val currentEffects = player.get(Keys.POTION_EFFECTS).orElse(emptyList())
            player.offer(Keys.POTION_EFFECTS, currentEffects.mergeWith(effectsToApply))
        }
        this.consumer?.invoke(player, pipeline, context)
        if (player.get(Keys.GAME_MODE).orElseGet(GameModes.NOT_SET) neq GameModes.CREATIVE) {
            val slot = context.requireContext(ContextKeys.USED_SLOT).fix()
            slot.poll(1)
            val restItem = this.restItemSupplier?.invoke()
            if (restItem != null) {
                if (slot.peek().isNotEmpty) {
                    player.inventory.primary.offer(restItem)
                } else {
                    slot.forceSet(restItem)
                }
            }
        }
        return BehaviorResult.SUCCESS
    }

    fun restItem(restItemSupplier: () -> ItemStack): ConsumableInteractionBehavior {
        this.restItemSupplier = restItemSupplier
        return this
    }

    fun consumer(consumer: (Player, BehaviorPipeline<Behavior>, BehaviorContext) -> Unit): ConsumableInteractionBehavior {
        this.consumer = consumer
        return this
    }
}
