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
package org.lanternpowered.api.ext

import org.lanternpowered.api.ResourceKeys
import org.lanternpowered.api.event.catalog.RegisterCatalogEvent
import org.lanternpowered.api.item.enchantment.EnchantmentType
import org.lanternpowered.api.item.enchantment.EnchantmentTypeBuilder
import org.lanternpowered.api.item.enchantment.enchantmentTypeOf
import org.lanternpowered.api.world.weather.Weather
import org.lanternpowered.api.world.weather.WeatherBuilder
import org.lanternpowered.api.world.weather.weatherOf

fun RegisterCatalogEvent<EnchantmentType>.register(id: String, fn: EnchantmentTypeBuilder.() -> Unit = {}): EnchantmentType =
        enchantmentTypeOf(ResourceKeys.activePlugin(id), fn).apply { register(this) }

fun RegisterCatalogEvent<Weather>.register(id: String, fn: WeatherBuilder.() -> Unit = {}): Weather =
        weatherOf(ResourceKeys.activePlugin(id), fn).apply { register(this) }
