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
package org.lanternpowered.server.effect.potion

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.api.entity.Entity
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternPotionEffectType(
        key: NamespacedKey,
        text: Text,
        val potionText: Text,
        val effectConsumer: (Entity, PotionEffect) -> Unit = { _,_ -> },
        private var instant: Boolean = false
) : DefaultCatalogType(key), PotionEffectType, TextRepresentable by text {

    constructor(key: NamespacedKey, effectConsumer: (Entity, PotionEffect) -> Unit = { _,_ -> }, instant: Boolean = false) :
            this(key,
                    translatableTextOf("effect.${key.namespace}.${key.value}"),
                    translatableTextOf("item.minecraft.potion.effect.${key.value}"),
                    effectConsumer, instant)

    fun instant() = apply { this.instant = true }

    override fun isInstant() = this.instant
}
