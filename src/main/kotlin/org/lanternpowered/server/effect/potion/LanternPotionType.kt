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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.item.potion.PotionType
import org.spongepowered.api.text.translation.Translatable
import org.spongepowered.api.text.translation.Translation

class LanternPotionType(
        key: ResourceKey, translationPart: String, override val internalId: Int, private val effects: List<PotionEffect>
) : DefaultCatalogType(key), PotionType, InternalCatalogType, Translatable by Translated("item.minecraft.potion.$translationPart") {

    /**
     * The [Translation] when a item stack is a lingering potion of this potion type.
     */
    val lingeringTranslation: Translation = tr("item.minecraft.lingering_potion.$translationPart")

    /**
     * The [Translation] when a item stack is a splash potion of this potion type.
     */
    val splashTranslation: Translation = tr("item.minecraft.splash_potion.$translationPart")

    /**
     * The [Translation] when a item stack is a tipped arrow that uses this potion type.
     */
    val tippedArrowTranslation: Translation = tr("item.minecraft.tipped_arrow.$translationPart")

    override fun getEffects() = this.effects
}
