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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.api.text.translation.Translatable
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.entity.Entity

class LanternPotionEffectType @JvmOverloads constructor(
        key: CatalogKey, override val internalId: Int, translation: Translation,
        private val potionTranslation: Translation,
        val effectConsumer: (Entity, PotionEffect) -> Unit = { _,_ -> },
        private var instant: Boolean = false
) : DefaultCatalogType(key), PotionEffectType, InternalCatalogType, Translatable by Translated(translation) {

    @JvmOverloads constructor(key: CatalogKey, internalId: Int,
                              effectConsumer: (Entity, PotionEffect) -> Unit = { _,_ -> }, instant: Boolean = false) :
            this(key, internalId,
                    tr("effect.${key.namespace}.${key.value}"),
                    tr("item.minecraft.potion.effect.${key.value}"),
                    effectConsumer, instant)

    fun instant() = apply { this.instant = true }

    override fun isInstant() = this.instant
    override fun getPotionTranslation() = this.potionTranslation
}
