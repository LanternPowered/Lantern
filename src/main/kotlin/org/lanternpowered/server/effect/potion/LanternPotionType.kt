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
package org.lanternpowered.server.effect.potion

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.text.translation.Translatable
import org.spongepowered.api.text.translation.Translation
import java.util.ArrayList

class LanternPotionType(key: CatalogKey, translationPart: String, override val internalId: Int) :
        DefaultCatalogType(key), PotionType, InternalCatalogType,
        Translatable by Translated("potion.$translationPart") {

    private val lingeringTranslation: Translation = tr("lingering_potion.$translationPart")
    private val splashTranslation: Translation = tr("splash_potion.$translationPart")
    private val tippedArrowTranslation: Translation = tr("tipped_arrow.$translationPart")

    private val potionEffects = ArrayList<PotionEffect>()

    override fun getEffects(): List<PotionEffect> = this.potionEffects
    override fun getLingeringTranslation(): Translation = this.lingeringTranslation
    override fun getSplashTranslation(): Translation = this.splashTranslation
    override fun getTippedArrowTranslation(): Translation = this.tippedArrowTranslation

    fun add(potionEffect: PotionEffect): LanternPotionType = apply { this.potionEffects.add(potionEffect) }
}
