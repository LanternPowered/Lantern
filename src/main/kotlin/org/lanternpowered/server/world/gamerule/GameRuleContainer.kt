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
package org.lanternpowered.server.world.gamerule

import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.getAllOf
import org.spongepowered.api.world.gamerule.GameRule
import org.spongepowered.api.world.gamerule.GameRuleHolder
import java.util.function.Consumer

@Suppress("UNCHECKED_CAST")
class GameRuleContainer : GameRuleHolder {

    private val values = hashMapOf<GameRule<*>, ValueHolder<*>>()

    override fun <V> getGameRule(gameRule: GameRule<V>): V {
        return (this.values[gameRule]?.value as? V) ?: gameRule.defaultValue
    }

    override fun <V> setGameRule(gameRule: GameRule<V>, value: V) {
        val holder = this.values.computeIfAbsent(gameRule) { ValueHolder(gameRule.defaultValue, arrayListOf()) } as ValueHolder<V>
        if (holder.value != value) {
            holder.listeners.forEach { it(value) }
        }
        holder.value = value
    }

    override fun getGameRules(): Map<GameRule<*>, *> {
        val gameRules = hashMapOf<GameRule<*>, Any>()
        for (gameRule in CatalogRegistry.getAllOf<GameRule<*>>()) {
            gameRules[gameRule] = this.values[gameRule]?.value ?: gameRule.defaultValue
        }
        return gameRules
    }

    /**
     * Adds a listener which tracks changes to the specified [GameRule].
     *
     * @param gameRule The game rule
     * @param listener The listener
     */
    fun <V> addGameRuleListener(gameRule: GameRule<V>, listener: Consumer<V>) = apply {
        addGameRuleListener(gameRule, listener::accept)
    }

    /**
     * Adds a listener which tracks changes to the specified [GameRule].
     *
     * @param gameRule The game rule
     * @param listener The listener
     */
    fun <V> addGameRuleListener(gameRule: GameRule<V>, listener: (V) -> Unit) = apply {
        val holder = this.values.computeIfAbsent(gameRule) { ValueHolder(gameRule.defaultValue, arrayListOf()) } as ValueHolder<V>
        holder.listeners.add(listener)
    }

    private data class ValueHolder<V>(
            var value: V,
            var listeners: MutableList<(V) -> Unit>
    )
}
