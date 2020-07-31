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
package org.lanternpowered.api.world

import org.spongepowered.api.world.gamerule.GameRule
import java.util.function.Supplier

fun <V> World.getGameRule(gameRule: Supplier<out GameRule<V>>): V =
        this.properties.getGameRule(gameRule)

fun <V> World.getGameRule(gameRule: GameRule<V>): V =
        this.properties.getGameRule(gameRule)
