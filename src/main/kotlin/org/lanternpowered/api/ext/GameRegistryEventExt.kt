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

import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.api.event.catalog.CatalogRegisterEvent
import org.lanternpowered.api.item.enchantment.EnchantmentType
import org.lanternpowered.api.item.enchantment.EnchantmentTypeBuilder
import org.lanternpowered.api.world.weather.Weather
import org.lanternpowered.api.world.weather.WeatherBuilder

fun CatalogRegisterEvent<EnchantmentType>.register(id: String, fn: EnchantmentTypeBuilder.() -> Unit = {}): EnchantmentType =
        EnchantmentType(CatalogKeys.activePlugin(id), fn).apply { register(this) }

fun CatalogRegisterEvent<Weather>.register(id: String, fn: WeatherBuilder.() -> Unit = {}): Weather =
        Weather(CatalogKeys.activePlugin(id), fn).apply { register(this) }
