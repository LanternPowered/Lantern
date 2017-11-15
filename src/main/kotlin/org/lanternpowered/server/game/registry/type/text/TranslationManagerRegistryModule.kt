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
package org.lanternpowered.server.game.registry.type.text

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.game.registry.EarlyRegistration
import org.lanternpowered.server.text.translation.CombinedTranslationManager
import org.lanternpowered.server.text.translation.LanternTranslationManager
import org.lanternpowered.server.text.translation.MinecraftTranslationManager
import org.spongepowered.api.registry.RegistryModule
import org.spongepowered.api.text.translation.locale.Locales

object TranslationManagerRegistryModule : RegistryModule {

    var translationManager: CombinedTranslationManager by initOnce()
            private set

    @EarlyRegistration
    override fun registerDefaults() {
        this.translationManager = CombinedTranslationManager()
        // Get the asset repository
        val assetRepository = Lantern.getAssetRepository()
        // Add the translation manager as a reload listener
        assetRepository.addReloadListener(this.translationManager)
        // Add the minecraft language file for defaults and
        // the client translations
        this.translationManager.addManager(MinecraftTranslationManager())
        val lanternTranslationManager = LanternTranslationManager()
        // Add the lantern languages
        lanternTranslationManager.addResourceBundle(assetRepository.get("lantern", "lang/en_us.json").get(), Locales.EN_US)
        lanternTranslationManager.addResourceBundle(assetRepository.get("lantern", "lang/nl_nl.json").get(), Locales.NL_NL)
        this.translationManager.addManager(lanternTranslationManager)
        this.translationManager.setDelegateManager(lanternTranslationManager)
    }
}
