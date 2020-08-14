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
package org.lanternpowered.server.text

import net.kyori.adventure.translation.TranslationRegistry
import net.kyori.adventure.util.UTF8ResourceBundleControl
import org.lanternpowered.api.locale.Locale
import org.lanternpowered.api.locale.Locales

import java.util.ResourceBundle

object TranslationRegistries {

    /**
     * The default translation registry that will be used
     * by plugins, lantern, etc.
     */
    val Default: TranslationRegistry = TranslationRegistry.get()

    /**
     * A translation registry which contains all the vanilla
     * minecraft translations.
     */
    val Minecraft: TranslationRegistry = LanternTranslationRegistry()

    /**
     * The translation registry that will attempt to translate everything,
     * including vanilla minecraft and other translations. Minecraft translations
     * can be overwritten through the default registry.
     */
    val All: UnmodifiableTranslationRegistry = ComposedTranslationRegistry(Default, Minecraft)

    /**
     * Loads the translation files.
     */
    fun init() {
        Default.registerBundle(Locales.EN_US, "data/lantern/lang/lantern_en_us.properties")
        Default.registerBundle(Locales.NL_NL, "data/lantern/lang/lantern_nl_nl.properties")

        Minecraft.registerBundle(Locales.EN_US, "data/minecraft/lang/minecraft_en_us.properties")
    }
}

private fun TranslationRegistry.registerBundle(locale: Locale, resourceBundlePath: String) {
    val resourceBundle = ResourceBundle.getBundle(
            resourceBundlePath, locale, UTF8ResourceBundleControl.get())
    this.registerAll(locale, resourceBundle, false)
    if (locale.variant.isNotEmpty())
        this.registerAll(Locale(locale.language, locale.country), resourceBundle, false)
    if (locale.country.isNotEmpty())
        this.registerAll(Locale(locale.language), resourceBundle, false)
}
