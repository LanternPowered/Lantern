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
package org.lanternpowered.server.game.registry.type.text

import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.game.registry.EarlyRegistration
import org.lanternpowered.server.game.registry.RegistryModule
import org.spongepowered.api.registry.RegistryModule
import org.spongepowered.api.text.translation.locale.Locales

object TranslationManagerRegistryModule : RegistryModule {

    lateinit var translationManager: CombinedTranslationManager
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
