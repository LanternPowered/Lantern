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
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.item.potion.PotionType

class LanternPotionType(
        key: NamespacedKey, translationPart: String, override val internalId: Int, private val effects: List<PotionEffect>
) : DefaultCatalogType(key), PotionType, InternalCatalogType,
        TextRepresentable by translatableTextOf("item.minecraft.potion.$translationPart") {

    /**
     * The [Text] when a item stack is a lingering potion of this potion type.
     */
    val lingeringTranslation: Text = translatableTextOf("item.minecraft.lingering_potion.$translationPart")

    /**
     * The [Text] when a item stack is a splash potion of this potion type.
     */
    val splashTranslation: Text = translatableTextOf("item.minecraft.splash_potion.$translationPart")

    /**
     * The [Text] when a item stack is a tipped arrow that uses this potion type.
     */
    val tippedArrowTranslation: Text = translatableTextOf("item.minecraft.tipped_arrow.$translationPart")

    override fun getEffects() = this.effects
}
