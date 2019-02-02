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
package org.lanternpowered.server.world.gamerule

import org.lanternpowered.api.Lantern
import org.lanternpowered.api.ext.*
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
            holder.listeners.forEach { it.invoke(value) }
        }
        holder.value = value
    }

    override fun getGameRules(): Map<GameRule<*>, *> {
        val gameRules = hashMapOf<GameRule<*>, Any>()
        for (gameRule in Lantern.registry.getAllOf(GameRule::class)) {
            gameRules[gameRule] = this.values[gameRule]?.value ?: gameRule.defaultValue
        }
        return gameRules
    }

    fun <V> addGameRuleListener(gameRule: GameRule<V>, listener: Consumer<V>) = apply {
        addGameRuleListener(gameRule, listener::accept)
    }

    fun <V> addGameRuleListener(gameRule: GameRule<V>, listener: (V) -> Unit) = apply {
        val holder = this.values.computeIfAbsent(gameRule) { ValueHolder(gameRule.defaultValue, arrayListOf()) } as ValueHolder<V>
        holder.listeners.add(listener)
    }

    private data class ValueHolder<V>(
            var value: V,
            var listeners: MutableList<(V) -> Unit>
    )
}
